#include "types.h"
#include "defs.h"
#include "param.h"
#include "memlayout.h"
#include "mmu.h"
#include "x86.h"
#include "proc.h"
#include "spinlock.h"

struct {
  struct spinlock lock;
  struct proc proc[NPROC];
} ptable;

static struct proc *initproc;

int nextpid = 1;
extern void forkret(void);
extern void trapret(void);

static void wakeup1(void *chan);
void init_priority(enum Queue_Level);
void priority_boost(struct proc*);
struct proc *find_mlfq(struct proc*,struct proc*);
void mlfq_value_change(struct proc*);
struct proc *find_thread(struct proc*,struct proc*);
void
pinit(void)
{
  initlock(&ptable.lock, "ptable");
}

// Must be called with interrupts disabled
int
cpuid() {
  return mycpu()-cpus;
}
int getppid(){
  return getppid();
}

// Must be called with interrupts disabled to avoid the caller being
// rescheduled between reading lapicid and running through the loop.
struct cpu*
mycpu(void)
{
  int apicid, i;
  
  if(readeflags()&FL_IF)
    panic("mycpu called with interrupts enabled\n");
  
  apicid = lapicid();
  // APIC IDs are not guaranteed to be contiguous. Maybe we should have
  // a reverse map, or reserve a register to store &cpus[i].
  for (i = 0; i < ncpu; ++i) {
    if (cpus[i].apicid == apicid)
      return &cpus[i];
  }
  panic("unknown apicid\n");
}

// Disable interrupts so that we are not rescheduled
// while reading proc from the cpu structure
struct proc*
myproc(void) {
  struct cpu *c;
  struct proc *p;
  pushcli();
  c = mycpu();
  p = c->proc;
  popcli();
  return p;
}

//PAGEBREAK: 32
// Look in the process table for an UNUSED proc.
// If found, change state to EMBRYO and initialize
// state required to run in the kernel.
// Otherwise return 0.
static struct proc*
allocproc(void)
{
  struct proc *p;
  char *sp;

  acquire(&ptable.lock);

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
  if((p->state==RUNNABLE ||p->state==RUNNING)&&p->scheduling==MLFQ&&p->mlfq.level==MLFQ_01&&p->thread==0){
	p->mlfq.priority=0;
}

    if(p->state == UNUSED)
      goto found;

}
  release(&ptable.lock);
  return 0;

found:
  p->state = EMBRYO;
  p->pid = nextpid++;

  release(&ptable.lock);
  // Allocate kernel stack.
  if((p->kstack = kalloc()) == 0){
    p->state = UNUSED;
    return 0;
  }
  sp = p->kstack + KSTACKSIZE;

  // Leave room for trap frame.
  sp -= sizeof *p->tf;
  p->tf = (struct trapframe*)sp;

  // Set up new context to start executing at forkret,
  // which returns to trapret.
  sp -= 4;
  *(uint*)sp = (uint)trapret;

  sp -= sizeof *p->context;
  p->context = (struct context*)sp;
  memset(p->context, 0, sizeof *p->context);
  p->context->eip = (uint)forkret;

  p->scheduling=MLFQ;

  memset(&p->mlfq,0, sizeof p->mlfq);
  memset(&p->stride,0,sizeof p->stride);
  if(p->pid>=3){
	virtual_mlfq.proc_num++;
}
  return p;
}

//PAGEBREAK: 32
// Set up first user process.
void
userinit(void)
{
  struct proc *p;
  extern char _binary_initcode_start[], _binary_initcode_size[];

  p = allocproc(); 
  initproc = p;
  if((p->pgdir = setupkvm()) == 0)
    panic("userinit: out of memory?");
  inituvm(p->pgdir, _binary_initcode_start, (int)_binary_initcode_size);
  p->sz = PGSIZE;
  memset(p->tf, 0, sizeof(*p->tf));
  p->tf->cs = (SEG_UCODE << 3) | DPL_USER;
  p->tf->ds = (SEG_UDATA << 3) | DPL_USER;
  p->tf->es = p->tf->ds;
  p->tf->ss = p->tf->ds;
  p->tf->eflags = FL_IF;
  p->tf->esp = PGSIZE;
  p->tf->eip = 0;  // beginning of initcode.S

  safestrcpy(p->name, "initcode", sizeof(p->name));
  p->cwd = namei("/");

  p->master=p;
  // this assignment to p->state lets other cores
  // run this process. the acquire forces the above
  // writes to be visible, and the lock is also needed
  // because the assignment might not be atomic.
  acquire(&ptable.lock);

  p->state = RUNNABLE;
  release(&ptable.lock);
}

// Grow current process's memory by n bytes.
// Return 0 on success, -1 on failure.
int
growproc(int n)
{
  uint sz;
  struct proc *curproc = myproc();
acquire(&ptable.lock);
  if(curproc->thread==1){
	curproc=curproc->master;
}

  sz = curproc->sz;
//  int index=curproc->number_heap;
//  curproc->grow_base[index]=sz;
  if(n > 0){
    if((sz = allocuvm(curproc->pgdir, sz, sz + n)) == 0)
      return -1;
  } else if(n < 0){
    if((sz = deallocuvm(curproc->pgdir, sz, sz + n)) == 0)
      return -1;
  }
//  curproc->grow_bound[index]=sz;
// curproc->number_heap=index+1;
  curproc->sz = sz;
  release(&ptable.lock);
  switchuvm(curproc);
  return 0;
}

// Create a new process copying p as the parent.
// Sets up stack to return as if from system call.
// Caller must set state of returned proc to RUNNABLE.
int
fork(void)
{
  int i, pid;
  struct proc *np;
  struct proc *curproc = myproc();
  // Allocate process.
  if((np = allocproc()) == 0){
    return -1;
  }
  // Copy process state from proc.
  if((np->pgdir = copyuvm(curproc->pgdir, curproc->sz)) == 0){
    kfree(np->kstack);
    np->kstack = 0;
    np->state = UNUSED;
    return -1;
  }
  np->sz = curproc->sz;
  np->parent =curproc;
  *np->tf = *curproc->tf;

  // Clear %eax so that fork returns 0 in the child.
  np->tf->eax = 0;

  for(i = 0; i < NOFILE; i++)
    if(curproc->ofile[i])
      np->ofile[i] = filedup(curproc->ofile[i]);
  np->cwd = idup(curproc->cwd);

  safestrcpy(np->name, curproc->name, sizeof(curproc->name));
  pid = np->pid;
  np->tid=curproc->tid;
  acquire(&ptable.lock);

  np->state = RUNNABLE;

  release(&ptable.lock);

  return pid;
}
void clear_thread(struct proc *p){
if(p->thread==1){
for(int i=0;i<40;i++){
if(p->master->avail_space[i]==0){
p->master->avail_space[i]=p->sz;
break;
}
}}
	kfree(p->kstack);
	p->pid=0;
	p->master=0;
	p->name[0]=0;
	p->killed=0;
	p->state=UNUSED;

	p->kstack=0;
	deallocuvm(p->pgdir,p->sz,(p->sz)-2*PGSIZE);
}
void
kill_thread(struct proc *curproc){
	struct proc *p;
int thread_num;
	while(1){
	thread_num=0;
	for(p=ptable.proc;p<&ptable.proc[NPROC];p++){
	if(p->pid==curproc->pid &&p->thread==1&&p!=curproc){
	if(p->state==ZOMBIE||p->state==SLEEPING){
	p->master->total_thread--;
	clear_thread(p);
}
	else{
	p->temp_killed=1;
	p->killed=1;
	thread_num++;
	wakeup1(p);}	
}
}
	if(thread_num==0){
	release(&ptable.lock);
	break;
}
	sleep(curproc,&ptable.lock);
}
}

// Exit the current process.  Does not return.
// An exited process remains in the zombie state
// until its parent calls wait() to find out it exited.
void
exit(void)
{
  struct proc *curproc = myproc();
  struct proc *p;
  struct proc *temp_master=0;
  int fd;
//If mlfq process exits, then decrease the number
  if(curproc->scheduling==MLFQ&&curproc->thread==0){
	virtual_mlfq.proc_num--;
}
  if(curproc == initproc)
    panic("init exiting");

acquire(&ptable.lock);
//if curproc is worker thread, kill all LWPs
//only One thread allows
if(curproc->thread==1&&curproc->temp_killed==0){
 	kill_thread(curproc);
	curproc->temp_killed=1;

 curproc->master->total_thread-=1;
	clear_thread(curproc);
}
else if(curproc->thread==0){
//if curproc is master
  kill_thread(curproc);
}
if(ptable.lock.locked==1){
release(&ptable.lock);
}

  // Close all open files.
  for(fd = 0; fd < NOFILE; fd++){
    if(curproc->ofile[fd]){
      fileclose(curproc->ofile[fd]);
      curproc->ofile[fd] = 0;
    }
  }

  begin_op();
  iput(curproc->cwd);
  end_op();
  curproc->cwd = 0;

  acquire(&ptable.lock);
  if(curproc->thread==0){
// Parent might be sleeping in wait().
  wakeup1(curproc->parent);

}
else{
// Master might be sleeping in wait().
if(curproc->master==0){
wakeup1(temp_master);
}
else{
  curproc->master->total_thread-=1;
  wakeup1(curproc->master);
}}
  // Pass abandoned children to init.
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
	//Wake up the first thread that calls exit().
	if(p->temp_killed==0&&p->thread==1&&p->pid==curproc->pid)
	wakeup1(p);
    if(p->parent == curproc){
      p->parent = initproc;
      if(p->state == ZOMBIE)
        wakeup1(initproc);
    }
  }

  // Jump into the scheduler, never to return.
  if(curproc->state!=UNUSED){
  curproc->state = ZOMBIE;
}
  sched();
  panic("zombie exit");
}


// Wait for a child process to exit and return its pid.
// Return -1 if this process has no children.
int
wait(void)
{
  struct proc *p;
  int havekids, pid;
  struct proc *curproc = myproc();
  
  acquire(&ptable.lock);
  for(;;){
    // Scan through table looking for exited children.
    havekids = 0;
    for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
      if(p->parent != curproc)
        continue;
      havekids = 1;
      if(p->state == ZOMBIE){
        // Found one.
        pid = p->pid;
        kfree(p->kstack);
        p->kstack = 0;
        freevm(p->pgdir);
        p->pid = 0;
        p->parent = 0;
        p->name[0] = 0;
        p->killed = 0;
        p->state = UNUSED;
	p->sz=0;
	for(int i=0;i<40;i++)
		p->avail_space[i]=0;

        release(&ptable.lock);
        return pid;
      }
    }

    // No point waiting if we don't have any children.
    if(!havekids || curproc->killed){
      release(&ptable.lock);
      return -1;
    }

    // Wait for children to exit.  (See wakeup1 call in proc_exit.)
    sleep(curproc, &ptable.lock);  //DOC: wait-sleep
  }
}
struct proc *find_mlfq(struct proc *p,struct proc *cur_proc){
 int mlfq01_first=0;
  int mlfq02_first=0;
  int mlfq03_first=0;
  int check_point=0;
  int check_point2=0;

  int proc_num=0;
	for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
        if(p->state != RUNNABLE ||p->scheduling == STRIDE||p->thread==1){
        continue;
}
	proc_num++;
//If process Level is MLFQ_01
	if(p->mlfq.level == MLFQ_01){
		if(mlfq01_first==0){
		cur_proc=p;
}
		else if(p->mlfq.priority <= cur_proc->mlfq.priority){
		cur_proc=p;
}	
        	check_point=1;
		mlfq01_first=1;
}
//If process Level is MLFQ_02
	else if(p->mlfq.level == MLFQ_02 && check_point==0){
		if(mlfq02_first==0){
 		cur_proc=p;
}
		else if(p->mlfq.priority <= cur_proc->mlfq.priority){
		cur_proc=p;
}
		mlfq02_first=1;
        	check_point2=1;
}
//If process level is MLFQ_03
	else if(p->mlfq.level==MLFQ_03&& check_point==0 &&check_point2==0){
          	if(mlfq03_first== 0){
		cur_proc=p;
}
		else if(p->mlfq.priority <= cur_proc->mlfq.priority){
		cur_proc=p;
        	}
	        mlfq03_first=1;
}
}

return cur_proc;
}
void priority_boost(struct proc *p){
       if(virtual_mlfq.total_ticks==100){
       for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
       if(p->state != RUNNABLE || p->scheduling == STRIDE){
       continue;
}
	p->flag=0;	
	p->mlfq.level=MLFQ_01;
	p->mlfq.priority=0;	
}
	virtual_mlfq.total_ticks=0;
}
}
void mlfq_value_change(struct proc *cur_proc){
struct proc *temp=cur_proc;
if(cur_proc->thread==1){
cur_proc=cur_proc->master;
}
switch(cur_proc->mlfq.level){
	case MLFQ_01:
		cur_proc->mlfq.time_quantum++;
		cur_proc->mlfq.allotment++;
		if(cur_proc->mlfq.time_quantum==5){
		cur_proc->mlfq.priority++;
		cur_proc->mlfq.time_quantum=0;
}
		if(cur_proc->mlfq.allotment==20){
		cur_proc->mlfq.level = MLFQ_02;
		cur_proc->mlfq.allotment=0;
		cur_proc->mlfq.priority=0;
		init_priority(MLFQ_02);
}
		break;	
	case MLFQ_02:
		cur_proc->mlfq.allotment++;
		cur_proc->mlfq.time_quantum++;
		if(cur_proc->mlfq.time_quantum==10){
		cur_proc->mlfq.priority++;
		cur_proc->mlfq.time_quantum=0;
}
		if(cur_proc->mlfq.allotment==40){
		cur_proc->mlfq.level = MLFQ_03;
		cur_proc->mlfq.allotment=0;
		cur_proc->mlfq.priority=0;
		cur_proc->mlfq.time_quantum=0;	
		init_priority(MLFQ_03);
}
		break;
	case MLFQ_03:
		cur_proc->mlfq.time_quantum++;
		if(cur_proc->mlfq.time_quantum==20){
		cur_proc->mlfq.priority++;
		cur_proc->mlfq.time_quantum=0;
}
	break;

}

cur_proc=temp;

}

//which thread(including master thread) schedule(Round-Robin)
struct proc *find_thread(struct proc *p,struct proc *cur_proc){
int compare_flag=10000000;
	for(p=ptable.proc;p<&ptable.proc[NPROC];p++){
	if(p->state!=RUNNABLE ||p->pid !=cur_proc->pid)
	continue;
       if(compare_flag>=p->flag){
	compare_flag=p->flag;
	cur_proc=p;
}
}
	return cur_proc;
}
void
scheduler(void)
{

  struct proc *p;
  struct cpu *c = mycpu();
  struct proc *cur_proc=0;
  struct proc *pre_proc=0;
//the number of process
  int pid_num=0;
 
//initialize the minimum_counter to MLFQ counter
  double minimum_counter=virtual_mlfq.counter;
  c->proc=0;
  for(;;){
    sti();

    acquire(&ptable.lock);
    if(virtual_mlfq.proc_num==0){
    minimum_counter=10000000;
}
    else{
	minimum_counter=virtual_mlfq.counter;
}

    for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
	if(p->pid>=3){
        pid_num++;}
//if thread variable is 1, that process is thread
      if(p->state != RUNNABLE||p->scheduling== MLFQ||p->thread==1){
        continue;}
//Compare the counter for choosing MLFQ or STRIDE process
	if(p->thread==0||minimum_counter >= p->stride.counter){
	cur_proc=p;
	minimum_counter=p->stride.counter;
}     
}
//when you boost your computer, use Round-Robin policy until new process starts
    if(pid_num==0){
	for(p=ptable.proc;p<&ptable.proc[NPROC]; p++){
	if(p->state != RUNNABLE)
	continue;	

	c->proc=p;
	switchuvm(p);
	p->state = RUNNING;

	swtch(&(c->scheduler),p->context);
	switchkvm();
	cur_proc=p;
	pre_proc=cur_proc;
}
}
//MLFQ Scheduling    
    else if(cur_proc==0){
	priority_boost(p);
//Choose the highest Level and highest priority process
	cur_proc=find_mlfq(p,cur_proc);
	if(cur_proc){
//Choose the thread of process
	cur_proc=find_thread(p,cur_proc);}
else{
	cur_proc=find_thread(p,pre_proc);
}
	c->proc=cur_proc;

        switchuvm(cur_proc);
        cur_proc->state = RUNNING;
        swtch(&(c->scheduler),cur_proc->context);
        switchkvm();


//change the mlfq process's variable value(allotment,time_quantum. etc)
	mlfq_value_change(cur_proc);
	cur_proc->flag++;
	virtual_mlfq.total_ticks++;
	pre_proc=cur_proc;
	if(cur_proc->thread==1){
	cur_proc=cur_proc->master;
}
 virtual_mlfq.counter+=(virtual_mlfq.stride)/(cur_proc->total_thread+1); 
         
 }
//Run the Stride Process and Increment counter
        else {
	cur_proc=find_thread(p,cur_proc);
	c->proc=cur_proc;
	
	switchuvm(cur_proc);
	cur_proc->state = RUNNING;
	
        swtch(&(c->scheduler),cur_proc->context);
	switchkvm();
	cur_proc->stride.time_quantum++;
	pre_proc=cur_proc;
        if(cur_proc->stride.time_quantum==5){
	if(cur_proc->thread==1){
	cur_proc=cur_proc->master;
}
	cur_proc->stride.counter+=cur_proc->stride.stride;}
    }
	cur_proc=0;
	c->proc=0;
	pid_num=0;
	release(&ptable.lock);
}}

//Make priority value of curlevel's process to zero
void init_priority(enum Queue_Level curlevel){
struct proc *p;
	for(p=ptable.proc;p<&ptable.proc[NPROC];p++){
	if( p->state != RUNNABLE|| p->scheduling ==STRIDE|| p->mlfq.level != curlevel){continue;}

	p->mlfq.priority=0;
}
}


// Enter scheduler.  Must hold only ptable.lock
// and have changed proc->state. Saves and restores
// intena because intena is a property of this
// kernel thread, not this CPU. It should
// be proc->intena and proc->ncli, but that would
// break in the few places where a lock is held but
// there's no process.
void
sched(void)
{
  int intena;
  struct proc *p = myproc();

  if(!holding(&ptable.lock))
    panic("sched ptable.lock");
  if(mycpu()->ncli != 1)
    panic("sched locks");
  if(p->state == RUNNING)
    panic("sched running");
  if(readeflags()&FL_IF)
    panic("sched interruptible");
  intena = mycpu()->intena;
  swtch(&p->context, mycpu()->scheduler);
  mycpu()->intena = intena;
}

// Give up the CPU for one scheduling round.
void
yield(void)
{
  acquire(&ptable.lock);  //DOC: yieldlock
  myproc()->state = RUNNABLE;
  sched();
  release(&ptable.lock);
}

// A fork child's very first scheduling by scheduler()
// will swtch here.  "Return" to user space.
void
forkret(void)
{
  static int first = 1;
  // Still holding ptable.lock from scheduler.
  release(&ptable.lock);

  if (first) {
    // Some initialization functions must be run in the context
    // of a regular process (e.g., they call sleep), and thus cannot
    // be run from main().
    first = 0;
    iinit(ROOTDEV);
    initlog(ROOTDEV);
  }

  // Return to "caller", actually trapret (see allocproc).
}
//Make scheduling stride
//Manage the stride's data in master thread for set_cpu_share
int
set_cpu_share(int cpu_share){
struct proc *cp=myproc();
int pid;
if(cp->thread==1){
cp=cp->master;
}
 if(virtual_mlfq.cpu_share+cpu_share>80){
	return -1;
}
 //Decrease  MLFQ process number
virtual_mlfq.proc_num--;

virtual_mlfq.cpu_share+=cpu_share;
virtual_mlfq.stride=1000 / (100-virtual_mlfq.cpu_share);
virtual_mlfq.counter=0;
cp->stride.cpu_share=cpu_share;
//total tickets are 1000
cp->stride.stride=(1000/cpu_share)/(cp->total_thread+1);
cp->mlfq.priority=0;
cp->mlfq.level= MLFQ_01;
cp->scheduling=STRIDE;
pid=cp->pid;
//setting counter=0 of all the process including virtual_mlfq
//Make all thread scheduling to STRIDE
 for(cp=ptable.proc;cp < &ptable.proc[NPROC];cp++){
	if(cp->state ==RUNNABLE && cp->pid==pid){
	cp->scheduling=STRIDE;
}
	if(cp->state != RUNNABLE|| cp->scheduling ==MLFQ||cp->thread==1){
	continue;
}
	cp->stride.counter=0;
}
//For the safety of Exceeding the large value of pre-setting variable
if(virtual_mlfq.proc_num==0)
virtual_mlfq.counter=0;

cp=0;
 return 0;
}


// Atomically release lock and sleep on chan.
// Reacquires lock when awakened.
void
sleep(void *chan, struct spinlock *lk)
{
  struct proc *p = myproc();
  if(p == 0)
    panic("sleep");

  if(lk == 0)
    panic("sleep without lk");

  // Must acquire ptable.lock in order to
  // change p->state and then call sched.
  // Once we hold ptable.lock, we can be
  // guaranteed that we won't miss any wakeup
  // (wakeup runs with ptable.lock locked),
  // so it's okay to release lk.
  if(lk != &ptable.lock){  //DOC: sleeplock0
    acquire(&ptable.lock);  //DOC: sleeplock1
    release(lk);
  }
  // Go to sleep.
  p->chan = chan;
  p->state = SLEEPING;
  sched();

  // Tidy up.
  p->chan = 0;

  // Reacquire original lock.
  if(lk != &ptable.lock){  //DOC: sleeplock2
    release(&ptable.lock);
    acquire(lk);
  }
}

//PAGEBREAK!
// Wake up all processes sleeping on chan.
// The ptable lock must be held.
static void
wakeup1(void *chan)
{
  struct proc *p;

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++)
    if(p->state == SLEEPING && p->chan == chan)
	p->state=RUNNABLE;

}

// Wake up all processes sleeping on chan.
void
wakeup(void *chan)
{
  acquire(&ptable.lock);
  wakeup1(chan);
  release(&ptable.lock);
}

// Kill the process with the given pid.
// Process won't exit until it returns
// to user space (see trap in trap.c).
int
kill(int pid)
{ 
  struct proc *p;
int thread=0;
 struct proc *curproc=myproc();
if(curproc->thread==1){
thread=1;
}
  acquire(&ptable.lock);
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->pid == pid && p->thread==thread&&p!=curproc){
      p->killed = 1;
      // Wake process from sleep if necessary.
      if(p->state == SLEEPING)
        p->state = RUNNABLE;
      release(&ptable.lock);
      return 0;
    }
  }
  release(&ptable.lock);
  return -1;
}

//PAGEBREAK: 36
// Print a process listing to console.  For debugging.
// Runs when user types ^P on console.
// No lock to avoid wedging a stuck machine further.
void
procdump(void)
{
  static char *states[] = {
  [UNUSED]    "unused",
  [EMBRYO]    "embryo",
  [SLEEPING]  "sleep ",
  [RUNNABLE]  "runble",
  [RUNNING]   "run   ",
  [ZOMBIE]    "zombie"
  };
  int i;
  struct proc *p;
  char *state;
  uint pc[10];

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->state == UNUSED)
      continue;
    if(p->state >= 0 && p->state < NELEM(states) && states[p->state])
      state = states[p->state];
    else
      state = "???";
    cprintf("%d %s %s", p->pid, state, p->name);
    if(p->state == SLEEPING){
      getcallerpcs((uint*)p->context->ebp+2, pc);
      for(i=0; i<10 && pc[i] != 0; i++)
        cprintf(" %p", pc[i]);
    }
    cprintf("\n");
  }
}
//Similar with fork()
int 
thread_create(thread_t *thread, void*(*start_routine)(void *),void *arg){
struct proc *curproc=myproc();
struct proc *p;
struct proc *np;
uint sz,sp, temp;
int i;
pde_t *pgdir;

if(curproc->thread==1){
curproc=curproc->master;
}

//Allocate process and kernel stack
if((np=allocproc())==0){
return -1;}

//when call thread_create, then this process is LWP(thread=1 means LWP)
*np->tf=*curproc->tf;

 for(i = 0; i < NOFILE; i++)
    if(curproc->ofile[i])
      np->ofile[i] = filedup(curproc->ofile[i]);
  np->cwd = idup(curproc->cwd);

  safestrcpy(np->name, curproc->name, sizeof(curproc->name));

sz=curproc->sz;

temp=sz+2*PGSIZE;
//Find available stack memory space
for(i=0;i<40;i++){
if(curproc->avail_space[i]!=0){
sz=curproc->avail_space[i]-2*PGSIZE;
break;
}
}
curproc->avail_space[i]=0;

acquire(&ptable.lock);

pgdir=curproc->pgdir;
if((sz=allocuvm(pgdir,sz,sz+2*PGSIZE))==0){
np->state=UNUSED;
return -1;
}
//thread process can't increment the number of MLFQ process
virtual_mlfq.proc_num--;
release(&ptable.lock);

//TO make guard page
//clearpteu(pgdir,(char*)(sz-2*PGSIZE));

if(temp==sz){
curproc->sz=sz;
}

sp=sz;
sp-=4;
*((uint*)sp)=(uint)arg;
sp-=4;
*((uint*)sp)=0xffffffff;

nextpid--;

np->thread=1;
np->scheduling=curproc->scheduling;
np->pid=curproc->pid;
np->sz=sz;
//np->parent=curproc->parent;
np->tf->eip=(uint)start_routine;//for return to function

//for stack location
np->tf->esp=sp;

np->master=curproc;
np->pgdir=pgdir;
//for setting the LWP that is worker thread
curproc->total_thread=curproc->total_thread+1;
if(curproc->scheduling==STRIDE){
curproc->stride.stride=1000/curproc->stride.cpu_share / curproc->total_thread;
}
np->tid=curproc->total_thread;
*thread=np->tid;
for(p=ptable.proc;p<&ptable.proc[NPROC];p++){
if(curproc->pid==p->pid){
p->flag=0;
}
}
acquire(&ptable.lock);

np->state=RUNNABLE;

release(&ptable.lock);
return 0;
}
//Similar with wait()
int
thread_join(thread_t thread,void **retval){
struct proc *curproc=myproc();
struct proc *p;
int master_pid=curproc->pid; 
if(curproc->thread==1){
return -1;
}
acquire(&ptable.lock);
for(;;){
  //Scan through table looking for proper thread
  for(p=ptable.proc; p<&ptable.proc[NPROC];p++){
  if(p->tid!=thread||p->pid!=master_pid)
    continue;
  if(p->state==ZOMBIE){
  //Found one.
  *retval=p->return_value;
  clear_thread(p);
  release(&ptable.lock);
  return 0;
}

}
  if(curproc->killed){
  release(&ptable.lock);
return -1;
}
sleep(curproc,&ptable.lock);//DOC:wait-sleep
}
return 0;
}
//Similar with exit
void thread_exit(void *retval){
struct proc *curproc=myproc();
int fd;
//CLose all open files.
  for(fd = 0; fd < NOFILE; fd++){
    if(curproc->ofile[fd]){
      fileclose(curproc->ofile[fd]);
      curproc->ofile[fd] = 0;
    }
  }
  begin_op();
  iput(curproc->cwd);
  end_op();
  curproc->cwd = 0;
 
 acquire(&ptable.lock);

 curproc->return_value=retval;
 //Master might be sleeping in wait()
 wakeup1(curproc->master);

//Jump into the scheduler,never to return
curproc->state= ZOMBIE;
sched();
panic("zombie exist");

}
void reset_inform(struct proc *cur){
	if(cur->scheduling==STRIDE){
	cur->scheduling=MLFQ;
	virtual_mlfq.proc_num++;
	virtual_mlfq.cpu_share-=cur->stride.cpu_share;
	virtual_mlfq.stride=1000/(100-virtual_mlfq.cpu_share);
	cur->stride.counter=0;
	cur->stride.priority=0;
	cur->stride.time_quantum=0;
	cur->stride.stride=0;	
}
	else{
	cur->mlfq.level=MLFQ_01;
	cur->mlfq.time_quantum=0;
	cur->mlfq.allotment=0;
	cur->mlfq.priority=0;
}

}

int  wake_proc(struct proc *cur){
 struct proc *p;
//struct proc *temp=cur;
//int havekids=0;
if(cur->thread==1){
	cur->parent=cur->master->parent;
	cur->tid=0;
//	temp=cur->master;
//	cur->thread=0;
	cur->flag=0;
	reset_inform(cur);
	wakeup1(cur->master);
}
acquire(&ptable.lock);
 if(cur->killed){
	release(&ptable.lock);
	return -1;
}

  for(p=ptable.proc;p<&ptable.proc[NPROC];p++){
	//if there is a child, make it's parent to cur
//	if(p->parent==temp){
//	p->parent=cur;	
//	havekids++;
//}
 	if(p->pid==cur->pid&&p->killed==1&&p!=cur){
	p->state=RUNNING;
}	
}
 release(&ptable.lock);
//temp=0;
p=0;

//if(havekids){
//wait();
//}
return 0;
}
void
temp_kill(struct proc *curproc){


struct proc *p;
 for(p=ptable.proc;p<&ptable.proc[NPROC];p++){
  if(p->pid==curproc->pid&&p!=curproc&&p->thread==1){
	p->killed=1;
	p->state=SLEEPING;
}
} 
}

