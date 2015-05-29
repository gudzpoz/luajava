#ifndef BUFFER_H
#define BUFFER_H
/**
 * String Buffer.
 */

#include <stdlib.h>
#include <string.h>

struct buffer {
    char *pos;      /* start position of string */
    char *last;     /* end position of string */
    char *start;    /* start of buffer */
    char *end;      /* end of buffer */
};

#define buffer_size(buf)      (buf->last - buf->pos)
#define buffer_available(buf) (buf->end - buf->last)
#define buffer_capacity(buf)  (buf->end - buf->start)

struct buffer *buffer_create(size_t size);
void buffer_shrink(struct buffer *buf);
int buffer_grow(struct buffer *buf, size_t extra);
void buffer_delete(struct buffer *buf);

#endif
