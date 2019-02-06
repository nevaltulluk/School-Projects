/**
 * Student Name = Neval Tüllük
 * Student Number = 2014400216
 * Compile status = compiling
 * Program Status = working
 * Notes = I compiled the code with "mpic++ main.cpp -o  main.o" comment
 * in a linux machine and run the code using "mpiexec -n N ./main.o inputfile.txt outputfile.txt beta pi". Also
 * the input file needs to be in the same directory with main.cpp and result
 * will be created in the same directory.
 */
#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <fstream>
#include <cmath>
#include <numeric>

using namespace std;
//MAIN PART

double beta;
double pi;

int main(int argc, char **argv) {
    string inputfile = argv[1];
    string outputfile = argv[2];
    beta = atof(argv[3]);
    pi = atof(argv[4]);
    //gamma variable for calculating acceptance possibility
    double gamma_ = 0.5 * log((1 - pi) / pi);
    // Initialize the MPI environment
    MPI_Init(NULL, NULL);
    // Find out rank, size
    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);

    int N = world_size - 1;
    int i, j;
    int size = 200;
    int rows = size / N;

    //MASTER PROCESSOR
    if (world_rank == 0) {
        //initialize the array to read the input file
        int arr[size * size];
        //initializing the input file stream
        ifstream input;
        input.open(inputfile);
        //initializing the output file stream
        ofstream output;
        output.open(outputfile);
        //reading from file to array
        for (int m = 0; m < size * size; m++) {
            input >> arr[m];
        }
        //initializing the array to read back from slave processors.
        //N is the number of the slave processors
        int final[N][rows * size];
        //Sending the input data to slave processors
        for (i = 1; i <= N; i++) {
            MPI_Send(&arr[size * rows * (i - 1)], (size * rows), MPI_INT, i, 0, MPI_COMM_WORLD);
        }
        //gathering the result data from slave processors, also writing them to the result file
        for (int k = 1; k <= N; ++k) {
            MPI_Recv(final[k - 1], (size * rows), MPI_INT, k, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            for (int c = 0; c < rows; ++c) {
                for (int m = 0; m < size; ++m) {
                    output << final[k - 1][c * size + m] << " ";
                }
                output << endl;
            }
        }

    } else {
        //SLAVE PROCESSORS
        int *subarr = NULL; //coming from main processor
        int *top = NULL; //coming from top processor
        int *bottom = NULL; //coming from bottom processor
        //allocating memory for arrays above
        subarr = (int *) malloc(sizeof(int) * size * rows);
        top = (int *) malloc(sizeof(int) * size);
        bottom = (int *) malloc(sizeof(int) * size);
        //initializing the result array that will be sent to the master procesor
        int result[size * rows];
        //receiving data from master processor
        //initial data receive
        MPI_Recv(subarr, size * rows, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        //initializing the 2D array Z to make the changes on
        int Z[rows][size];
        //copying initial array to Z in a 1D to 2D manner
        for (int l = 0; l < rows; ++l) {
            for (int k = 0; k < size; ++k) {
                Z[l][k] = subarr[size * l + k];
            }

        }
        //main loop of the slave processor
        for (int m = 0; m < 500000; ++m) {
            // 2 random coordinate is choosen in Z
            int rand_x = rand() % (size); //X coordinate
            int rand_y = rand() % (rows); //Y coordinate
            //these integers are the neigbours of the choosen pixel
            //i1 i2 i3
            //i4 Cur i5
            //i6 i7 i8
            //where Cur is the current pixel
            int i1, i2, i3, i4, i5, i6, i7, i8;
            //this is a long if else block indeed but all it does to assign corresponding
            //pixels to the corresponding integer

            //all corner cases are handled for example: when the y coordinate of the pixel is
            //on the top of the frame then the corresponding i1 i2 and i3 are choosen from
            //the pixels coming from the neigbour slave processor or left empty if the current
            //slave is the slave number 1 since its on the top of the picture and so on
            if (rand_x == 0) {
                i1 = 0;
                i4 = 0;
                i6 = 0;
                i5 = Z[rand_y][rand_x + 1];
                if (rand_y == 0) {
                    if (world_rank == 1) {
                        i2 = 0;
                        i3 = 0;
                    } else {
                        i2 = top[rand_x];
                        i3 = top[rand_x + 1];
                    }
                } else if (rand_y == rows - 1) {
                    if (world_rank == world_size - 1) {
                        i7 = 0;
                        i8 = 0;
                    } else {
                        i7 = bottom[rand_x];
                        i8 = bottom[rand_x + 1];
                    }
                } else {
                    i2 = Z[rand_y - 1][rand_x];
                    i3 = Z[rand_y - 1][rand_x + 1];
                    i7 = Z[rand_y + 1][rand_x];
                    i8 = Z[rand_y + 1][rand_x + 1];

                }
            } else if (rand_x == size - 1) {
                i3 = 0;
                i5 = 0;
                i8 = 0;
                i4 = Z[rand_y][rand_x - 1];
                if (rand_y == 0) {
                    if (world_rank == 1) {
                        i1 = 0;
                        i2 = 0;
                    } else {
                        i1 = top[rand_x - 1];
                        i2 = top[rand_x];
                    }
                } else if (rand_y == rows - 1) {
                    if (world_rank == world_size - 1) {
                        i6 = 0;
                        i7 = 0;
                    } else {
                        i6 = bottom[rand_x - 1];
                        i7 = bottom[rand_x];
                    }
                } else {
                    i1 = Z[rand_y - 1][rand_x - 1];
                    i2 = Z[rand_y - 1][rand_x];
                    i6 = Z[rand_y + 1][rand_x - 1];
                    i7 = Z[rand_y + 1][rand_x];

                }
            } else if (rand_y == 0) {
                i7 = Z[rand_y + 1][rand_x];
                if (world_rank == 0) {
                    i1 = 0;
                    i2 = 0;
                    i3 = 0;
                    if (rand_x == 0) {
                        i4 = 0;
                        i6 = 0;
                        i5 = Z[rand_y][rand_x + 1];
                        i8 = Z[rand_y + 1][rand_x + 1];
                    } else if (rand_x == size - 1) {
                        i4 = Z[rand_y][rand_x - 1];
                        i6 = Z[rand_y + 1][rand_x - 1];
                        i5 = 0;
                        i8 = 0;
                    } else {
                        i4 = Z[rand_y][rand_x - 1];
                        i6 = Z[rand_y + 1][rand_x - 1];
                        i5 = Z[rand_y][rand_x + 1];
                        i8 = Z[rand_y + 1][rand_x + 1];
                    }
                } else {
                    i2 = top[rand_x];
                    if (rand_x == 0) {
                        i1 = 0;
                        i3 = top[rand_x + 1];
                        i4 = 0;
                        i6 = 0;
                        i5 = Z[rand_y][rand_x + 1];
                        i8 = Z[rand_y + 1][rand_x + 1];
                    } else if (rand_x == size - 1) {
                        i1 = top[rand_x - 1];
                        i3 = 0;
                        i4 = Z[rand_y][rand_x - 1];
                        i6 = Z[rand_y + 1][rand_x - 1];
                        i5 = 0;
                        i8 = 0;
                    } else {
                        i1 = top[rand_x - 1];
                        i3 = top[rand_x + 1];
                        i4 = Z[rand_y][rand_x - 1];
                        i6 = Z[rand_y + 1][rand_x - 1];
                        i5 = Z[rand_y][rand_x + 1];
                        i8 = Z[rand_y + 1][rand_x + 1];
                    }

                }
            } else if (rand_y == rows - 1) {
                i2 = Z[rand_y - 1][rand_x];
                if (world_rank == world_size - 1) {
                    i6 = 0;
                    i7 = 0;
                    i8 = 0;
                    if (rand_x == 0) {
                        i1 = 0;
                        i4 = 0;
                        i3 = Z[rand_y - 1][rand_x + 1];
                        i5 = Z[rand_y][rand_x + 1];
                    } else if (rand_x == size - 1) {
                        i3 = 0;
                        i5 = 0;
                        i1 = Z[rand_y - 1][rand_x - 1];
                        i4 = Z[rand_y][rand_x - 1];
                    } else {
                        i1 = Z[rand_y - 1][rand_x - 1];
                        i4 = Z[rand_y][rand_x - 1];
                        i3 = Z[rand_y - 1][rand_x + 1];
                        i5 = Z[rand_y][rand_x + 1];
                    }
                } else {
                    i7 = bottom[rand_x];
                    if (rand_x == 0) {
                        i1 = 0;
                        i4 = 0;
                        i6 = 0;
                        i2 = Z[rand_y - 1][rand_x];
                        i3 = Z[rand_y - 1][rand_x + 1];
                        i8 = bottom[rand_x + 1];
                    } else if (rand_x == size - 1) {
                        i3 = 0;
                        i5 = 0;
                        i8 = 0;
                        i1 = Z[rand_y - 1][rand_x - 1];
                        i4 = Z[rand_y][rand_x - 1];
                        i6 = bottom[rand_x - 1];
                    } else {
                        i1 = Z[rand_y - 1][rand_x - 1];
                        i4 = Z[rand_y][rand_x - 1];
                        i6 = bottom[rand_x - 1];
                        i2 = Z[rand_y - 1][rand_x];
                        i3 = Z[rand_y - 1][rand_x + 1];
                        i8 = bottom[rand_x + 1];
                    }
                }
            } else {
                i1 = Z[rand_y - 1][rand_x - 1];
                i2 = Z[rand_y - 1][rand_x];
                i3 = Z[rand_y - 1][rand_x + 1];
                i4 = Z[rand_y][rand_x - 1];
                i5 = Z[rand_y][rand_x + 1];
                i6 = Z[rand_y + 1][rand_x - 1];
                i7 = Z[rand_y + 1][rand_x];
                i8 = Z[rand_y + 1][rand_x + 1];
            }
            //the acceptancy probabilty is calculated here
            double delta_e = -2 * gamma_ * subarr[rand_y * size + rand_x] * Z[rand_y][rand_x]
                             - 2 * beta * Z[rand_y][rand_x] * (i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8);
            //this is a random acceptance limit
            double random_accept = (rand() % 1000) / 100.0;

            //probabilistic acceptance
            if (log(random_accept) < delta_e) {
                Z[rand_y][rand_x] = -Z[rand_y][rand_x];
            }
            //SEND AND RECEIVE PART OF THE SLAVE PROCESSORS

            //firstly the odd numbered processors sends their first row to even numbered
            //slave processors which are above them and the even numbered processors
            //receive that data to their bottom array

            //secondly the odd numbered processors sends their last row data to even numbered
            //processors below them and the even numbered processors receive data to their top
            //array

            //thirdly the even numbered processors sends their first row to the odd numbered
            //processor above them and the corresponding odd numbered processor receives the
            //data in their bottom array

            //and lastly the even numbered processors send their last row to the odd numbered
            //processors below them and the odd numbered processors receive the data to their
            //top array

            //so there is never a deadlock

            //Even processors
            if (world_rank % 2 == 0) {
                if (world_rank < world_size - 1) {
                    MPI_Recv(top, size, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD,
                             MPI_STATUS_IGNORE); // RECV even <- odd from up
                    MPI_Recv(bottom, size, MPI_INT, world_rank + 1, 0, MPI_COMM_WORLD,
                             MPI_STATUS_IGNORE); // RECV even <- odd from down
                    MPI_Send(&Z[0], size, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD);//SEND even -> odd sent to up
                    MPI_Send(&Z[rows - 1], size, MPI_INT, world_rank + 1, 0,
                             MPI_COMM_WORLD);//SEND even -> odd to down

                }
                //last processor
                else if (world_rank == world_size - 1) {
                    MPI_Recv(top, size, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD,
                             MPI_STATUS_IGNORE); // RECV last <- last-1 from up
                    //last doesnt receive from down
                    MPI_Send(&Z[0], size, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD); // SEND last -> last-1 to up
                    //last doesnt send to down
                }
            }
            //Odd processors
            else {
                //first processor
                if (world_rank == 1) {
                    MPI_Send(&Z[rows - 1], size, MPI_INT, world_rank + 1, 0,
                             MPI_COMM_WORLD); // SEND 1-> 2 to down
                    //first doesnt send up
                    MPI_Recv(bottom, size, MPI_INT, 2, 0, MPI_COMM_WORLD,
                             MPI_STATUS_IGNORE);//RECV 1 <- 2 from down
                    //first doesnt receive from up


                } else if (world_rank < world_size - 1) {
                    MPI_Send(&Z[rows - 1], size, MPI_INT, world_rank + 1, 0,
                             MPI_COMM_WORLD); // SEND odd -> even to down
                    MPI_Send(&Z[0], size, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD);// SEND odd -> even to up
                    MPI_Recv(bottom, size, MPI_INT, world_rank + 1, 0, MPI_COMM_WORLD,
                             MPI_STATUS_IGNORE); // RECV odd <- even from down
                    MPI_Recv(top, size, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD,
                             MPI_STATUS_IGNORE); // RECV odd <- even from up

                }
                //last processor
                else if (world_rank == world_size - 1) {
                    //last doesnt send down
                    MPI_Send(&Z[0], size, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD);// SEND last -> last-1 to up
                    //last doesnt receive from down
                    MPI_Recv(top, size, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD,
                             MPI_STATUS_IGNORE); // RECV last from up

                }
            }
        }
        //the 2D Z array is turned into 1D result array since its easier to send this way
        for (int n = 0; n < rows; ++n) {
            for (int k = 0; k < size; ++k) {
                result[size * n + k] = Z[n][k];
            }
        }
        //slave processors send their data to master processor
        MPI_Send(&result, size * rows, MPI_INT, 0, 0, MPI_COMM_WORLD);
        //deallocating the space
        delete subarr;
        delete top;
        delete bottom;
    }
    MPI_Finalize();
}
