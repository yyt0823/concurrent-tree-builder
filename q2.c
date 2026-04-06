#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <omp.h>

#define NUM_STATES 4
#define S0 0
#define S1 1
#define S2 2
#define S3 3

// for each thread we want it to return a table of start and end state along with their count
typedef struct
{
    int counts[NUM_STATES];
    int end_states[NUM_STATES];
} ThreadResult;

int is19(char c) { return c >= '1' && c <= '9'; }
int is09af(char c) { return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'); }
int is19af(char c) { return (c >= '1' && c <= '9') || (c >= 'a' && c <= 'f'); }

// build the DFA
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
            return S3;
        (*matches)++;
        return S0;
    }
    return S0;
}

// Generate a random string of length n and add the EOF symbol
char *gen_string(int n)
{
    char charset[] = {'0', '0', '1', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                      'a', 'b', 'c', 'd', 'e', 'f', 'x', 'x', 'y'};
    int charset_size = 21;
    char *str = malloc(n + 1);
    for (int i = 0; i < n; i++)
        str[i] = charset[rand() % charset_size];
    str[n] = '\0';
    return str;
}

// the run dfa with some segment of the string function
void run_segment(char *str, int lo, int hi, int init_state, int *end_state, int *count)
{
    int state = init_state;
    *count = 0;
    for (int i = lo; i < hi; i++)
        state = dfa_step(state, str[i], count);
    *end_state = state;
}

int main(int argc, char *argv[])
{
    int t = atoi(argv[1]); // number of optimistic threads
    int n = atoi(argv[2]); // string length
    srand(42);
    char *str = gen_string(n);
    // naive way, for testing only
    // int seg = (n + 1) / (t + 1);
    // int naive_end, naive_count;
    // run_segment(str, 0, seg, S0, &naive_end, &naive_count);

    // printf("naive count: %d, end state: %d\n", naive_count, naive_end);

    ThreadResult results[t + 1];
    memset(results, 0, sizeof(results));
    double start_time = omp_get_wtime();
    #pragma omp parallel num_threads(t + 1)
    {
        int id = omp_get_thread_num();
        int seg = (n + 1) / (t + 1);
        int lo = id * seg;
        int hi = (id == t) ? n + 1 : (id + 1) * seg;
        if (id == 0)
        {
            run_segment(str, lo, hi, S0, &results[0].end_states[S0], &results[0].counts[S0]);
        }
        else
        {
            for (int s = 0; s < NUM_STATES; s++)
            {
                run_segment(str, lo, hi, s, &results[id].end_states[s], &results[id].counts[s]);
            }
        }
    }

    // now we use the built table and do a final pass to get the total count
    int total_count = 0;
    int current_state = S0;
    for (int i = 0; i < t + 1; i++)
    {
        total_count += results[i].counts[current_state];
        current_state = results[i].end_states[current_state];
    }

    double end_time = omp_get_wtime();
    printf("%d\n", total_count);
    printf("%.3f\n", (end_time - start_time) * 1000);

    return 0;
}
