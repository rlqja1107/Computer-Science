// Per-CPU state
struct cpu {
  uchar apicid;                // Local APIC ID
  struct context *scheduler;   // swtch() here to enter scheduler
  struct taskstate ts;         // Used by x86 to find stack for interrupt
  struct segdesc gdt[NSEGS];   // x86 global descriptor table
  volatile uint started;       // Has the CPU started?
  int ncli;                    // Depth of pushcli nesting.
  int intena;                  // Were interrupts enabled before pushcli?
  struct proc *proc;           // The process running on this cpu or null
};

extern struct cpu cpus[NCPU];
extern int ncpu;

//PAGEBREAK: 17
// Saved registers for kernel context switches.
// Don't need to save all the segment registers (%cs, etc),
// because they are constant across kernel contexts.
// Don't need to save %eax, %ecx, %edx, because the
// x86 convention is that the caller has saved them.
// Contexts are stored at the bottom of the stack they
// describe; the stack pointer is the address of the context.
// The layout of the context matches the layout of the stack in swtch.S
// at the "Switch stacks" comment. Switch doesn't save eip explicitly,
// but it is on the stack and allocproc() manipulates it.
struct context {
  uint edi;
  uint esi;
  uint ebx;
  uint ebp;
  uint eip;
};


struct {//for comparing cpu_share with others
  int cpu_share;
  int stride;
  int  counter;
  int total_ticks;
  int proc_num;
}virtual_mlfq;
enum Queue_Level {MLFQ_01,MLFQ_02,MLFQ_03};
struct mlfq{
  enum Queue_Level level;
  int time_quantum;
  int allotment;
  int priority;//when you use round-robin policy, use this proiority	
};
struct stride{
  int counter;
  int stride;
  int cpu_share;
  int priority;
  int time_quantum;
};

enum procstate { UNUSED, EMBRYO, SLEEPING, RUNNABLE, RUNNING, ZOMBIE };
enum scheduling {MLFQ,STRIDE};
// Per-process state
struct proc {
  uint sz;                     // Size of process memory (bytes)
  pde_t* pgdir;                // Page table
  char *kstack;                // Bottom of kernel stack for this process
  enum scheduling scheduling;  // MLFQ or STRIDE scheduling		
  enum procstate state;        // Process state
  int pid;                     // Process ID
  struct proc *parent;         // Parent process
  struct trapframe *tf;        // Trap frame for current syscall
  struct context *context;     // swtch() here to run process
  struct mlfq mlfq;           // stored mlfq data
  struct stride stride;       // storedstride data
  void *chan;                  // If non-zero, sleeping on chan
  int killed;                  // If non-zero, have been killed
  struct file *ofile[NOFILE];  // Open files
  struct inode *cwd;           // Current directory
  char name[16];               // Process name (debugging)
  int total_thread;	       // process's total thread number
  uint grow_base[20];	       //Base address for growing memory
  uint grow_bound[20];	       //Bound address for growing memory
  uint avail_space[40];      //Available Space(address) for using memory
  int number_heap;             //the number of heap(i.e index of grow_bound)
  //relate to thread
  int temp_killed;            //For checking thread kill
  int total_flag;
  int flag;                    //for Round-Robin 
  int tid;		       //thread number	
  int thread;		       //if thread then 1,default master
//  uint locate;  	       //to find thread stack
  struct proc *master;          // thread's master proc
  void* return_value;          //for saving the return value of thread
};

// Process memory is laid out contiguously, low addresses first:
//   text
//   original data and bss
//   fixed-size stack
//   expandable heap
