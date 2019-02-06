
#include <pthread.h>
#include <stdio.h>
#include <cstdlib>
#include <iostream>
#include <zconf.h>
#include <fstream>

using namespace std;
#define MAX_SIZE 200
pthread_mutex_t m = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t m1 = PTHREAD_MUTEX_INITIALIZER;
int reservation_list[MAX_SIZE];

//arguments struct to pass arguments to threads.
struct thread_arg {
    int id;
    int a;
    int b = -1;
    ofstream *outfile;
};
struct thread_arg args[MAX_SIZE];
//for client thread
void *runner(void *param);
//for server thread
void *reserve(void *seat_no);

int main(int argc, char *argv[]) {
    ofstream out;
    out.open("output.txt");

    int seats = atoi(argv[1]);

    int i;
    pthread_t tid[seats];
    //checks arguments validity
    if (argc != 2) {
        fprintf(stderr, "usage: a.out <integer value>\n");
        return -1;
    }

    if (atoi(argv[1]) < 49) {
        fprintf(stderr, "Argument %d must be >= 50 \n", atoi(argv[1]));
        return -1;
    }
    if (atoi(argv[1]) > 200) {
        fprintf(stderr, "Argument %d must be >= 200 \n", atoi(argv[1]));
        return -1;
    }
    for(int i=0; i<seats; i++) {//initialize the necessary arguments for client threads, and send the ofstream address for the logging to a file.
        args[i].id = i+1;
        args[i].a = seats;
        args[i].outfile = &out;
        pthread_create(&tid[i], NULL, &runner, &args[i]);//create threads
    }


    // now wait for the thread to exit
    for (int j = 0; j < seats; ++j) {
        pthread_join(tid[j], NULL);
    }
    out.close();

}

//client thread start function
void *runner(void *arg) {

    struct thread_arg * input = static_cast<thread_arg *>(arg);
    int id = input->id;
    int num_of_seats = input->a;
    int sleep_time = 50 + rand() % 151; //random sleep time for 50-150 ns
    usleep(sleep_time*100);
    //server thread creation
    pthread_t server_id;
    pthread_create(&server_id, NULL, &reserve, &args[id-1]);
    pthread_mutex_lock(&m);
    ///////////////////////CRITICAL SECTION 1 STARTS///////////////////

    //finds an available seat, an available seat should be not reserved and not invalidated (picked by another thread)
    bool isAvailable = true;
    int available_seat_number = -1;
    while(isAvailable){
        int random_seat = (rand() % num_of_seats) + 1;
        if (reservation_list[random_seat-1] == 0){
            available_seat_number = random_seat;
            isAvailable = false;
        }
    }

    reservation_list[available_seat_number-1] = -1; //seat is invalidated
    input-> b = available_seat_number; //the available seat number is initialized to the corresponding args struct to be passed to server thread.

    ///////////////////////CRITICAL SECTION 1 ENDS/////////////////////
    pthread_mutex_unlock(&m);



}
//server thread start function
void *reserve (void * arg){
    struct thread_arg * input = static_cast<thread_arg *> (arg);
    while(input->b == -1);//server thread waits until its client thread decides on a available seat number
    pthread_mutex_lock(&m1);
    ///////////////////////CRITICAL SECTION 2 STARTS///////////////////
    int id = input->id;
    int seat_no = input->b;
    int available_seat_index = seat_no-1;
    ofstream *out = (input->outfile);
    if (reservation_list[available_seat_index] == -1){
        reservation_list[available_seat_index] == 1; //reservation done
        *out << "Client" << id << " reserves " << "Seat" << seat_no << endl ;
    }
    else{
        cout << "reservation failed";
    }
    ///////////////////////CRITICAL SECTION 2 ENDS/////////////////////
    pthread_mutex_unlock(&m1);
}
