#include "types.h"
#include "stat.h"
#include "user.h"
int global_counter;
# define num 5
void* start_routine(void *arg){
int local_max=*(int*)arg;
int local_counter=0;
int i=0;
for(i=0;i<local_max;i++){
global_counter++;
local_counter++;
}
thread_exit((void*)local_counter);

return 0;
}
int main(int argc,char *argv[]){
int i=0;
int thread_max=50;
thread_t pid[num];
int ret_vals[num];
for(i=0;i<num;i++){
thread_create(&pid[i],start_routine,(void*)&thread_max);
}
for(i=0;i<num;i++)
{
thread_join(pid[i],(void *)&ret_vals[i]);

}
for(i=0;i<num;i++){
printf(1,"%d thread's local counter: %d\n",i,ret_vals[i]);
}
printf(1,"Global Counter: %d\n",global_counter);
exit();
}
