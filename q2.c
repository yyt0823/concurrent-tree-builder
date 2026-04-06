#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <omp.h>

#define NUM_STATES 5
#define S0 0
#define S1 1
#define S2 2
#define S3 3
#define S4 4

int is19(char c) { return c >= '1' && c <= '9'; }
int is09af(char c) { return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'); }
int is19af(char c) { return (c >= '1' && c <= '9') || (c >= 'a' && c <= 'f'); }

int dfa_step(int state, char c, int *matches)
    {
        switch (state)
        {
        case S0:
            return is19(c) ? S1 : S0;
        case S1:
            return is19af(c) ? S2 : S0;
        case S2:
            return is09af(c) ? S3 : S0;
        case S3:
            if (is09af(c))
                return S3; // self-loop
            (*matches)++;
            return S0;
        }
        return S0;
    }

int main(int argc, char *argv[])
{
    int t = atoi(argv[1]); // number of optimistic threads
    int n = atoi(argv[2]); // string length
    return 0;

    // build the DFA

    
}
