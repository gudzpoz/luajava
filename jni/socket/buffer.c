#include "buffer.h"

/**
 * Create a buffer of given size.
 */
struct buffer *
buffer_create(size_t size)
{
    struct buffer *buf = malloc(sizeof(*buf));
    if (!buf)
        return NULL;

    buf->start = malloc(size);
    if (!buf->start) {
        free(buf);
        return NULL;
    }

    buf->pos = buf->start;
    buf->last = buf->start;
    buf->end = buf->start + size;

    return buf;
}

/**
 * Shrink the buffer.
 *
 * Move string to starting point of buffer.
 */
void
buffer_shrink(struct buffer *buf)
{
    memmove(buf->start, buf->pos, buf->last - buf->pos);
    buf->last = buf->start + (buf->last - buf->pos);
    buf->pos = buf->start;
}

/**
 * Grow buffer extra size.
 */
int
buffer_grow(struct buffer *buf, size_t extra)
{
    if (extra <= 0)
        return 0;

    size_t size = buffer_capacity(buf) + extra;

    size_t pos_off = buf->pos - buf->start;
    size_t last_off = buf->last - buf->start;
        
    buf->start = realloc(buf->start, size);
    if (buf->start == NULL)
        return -1;

    buf->pos = buf->start + pos_off;
    buf->last = buf->start + last_off;
    buf->end = buf->start + size;

    return 0;
}

/**
 * Delete the buffer.
 */
void
buffer_delete(struct buffer *buf)
{
    if (buf->start) free(buf->start);
    buf->start = NULL;
    free(buf);
}
