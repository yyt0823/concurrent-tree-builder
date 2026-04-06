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

// Generate a random string of length n and add the EOF symbol
char *gen_string(int n)
{
    char charset[] = {'0', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                      'a', 'b', 'c', 'd', 'e', 'f', 'x', 'x', 'y'};
    int charset_size = 20;
    char *str = malloc(n + 1); 
    for (int i = 0; i < n; i++)
        str[i] = charset[rand() % charset_size];
    str[n] = '\0'; 
    return str;
}


int main(int argc, char *argv[])
{
    int t = atoi(argv[1]); // number of optimistic threads
    int n = atoi(argv[2]); // string length
    srand(42);                                                                                                                                           
    char *str = gen_string(n);
    printf("%s\n", str);
    return 0;

    // build the DFA
}
