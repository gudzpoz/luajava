#include "timeout.h"
#include <time.h>
#include <sys/time.h>

/**
 * Returns current time in ms. (since 1970)
 */
double
timeout_gettime(void)
{
    struct timeval v;
    gettimeofday(&v, NULL);
    return v.tv_sec + v.tv_usec / 1.0e6;
}

/**
 * Init timeout structure.
 */
void
timeout_init(struct timeout *tm, double timeout)
{
    tm->tm_timeout = timeout;
    if (tm->tm_timeout <= 0) {
        tm->tm_deadline = -1;
    } else {
        tm->tm_deadline = timeout_gettime() + tm->tm_timeout;
    }
}

/**
 * Determine how much time we have left.
 *
 * Returns the number of ms left or -1 if there is no time limit.
 */
double
timeout_left(struct timeout *tm)
{
    if (tm->tm_timeout <= 0) {
        return -1;
    } else {
        double left = tm->tm_deadline - timeout_gettime();
        if (left < 0.0) left = 0.0;
        return left;
    }
}
