#include "types.h"
#include "stat.h"
#include "user.h"

#define NUM_THREAD 10
int gcnt;
void *racingthreadmain(void*);
int
main(int argc,char *argv[])
{
  thread_t threads[NUM_THREAD];
  int i;
  void *retval;

  for (i = 0; i < NUM_THREAD; i++){
    if (thread_create(&threads[i],racingthreadmain, (void*)i) != 0){
      printf(1, "panic at thread_create\n");
      return -1;
    }
  }
  for (i = 0; i < NUM_THREAD; i++){
    if (thread_join(threads[i], &retval) != 0 || (int)retval != i+1){
      printf(1, "panic at thread_join\n");
      return -1;
    }

	
}
  printf(1,"%d\n", gcnt);
 exit();
}


void *racingthreadmain(void *arg)
{
  int tid = (int) arg;
  int i;
  //int j;
  int tmp;
  for (i = 0; i < 1000; i++){
    tmp = gcnt;
    tmp++;
    gcnt = tmp;
  }

  thread_exit((void *)(tid+1));
return 0;
}
