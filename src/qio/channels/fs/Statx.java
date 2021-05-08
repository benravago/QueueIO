package qio.channels.fs;

import java.nio.ByteBuffer;

public class Statx {

  public final byte[] st = new byte[0];  // sizeof(struct statx)

  public final ByteBuffer buf = ByteBuffer.wrap(st); // set little-endian

  public int mode() { return buf.getInt(0); } // offsetof(struct statx, st_mode)
  // ...

}
/*
    TODO: generate offsetof(struct statx, ...)

    struct statx {
        __u32 stx_mask;             // Mask of bits indicating filled fields
        __u32 stx_blksize;          // Block size for filesystem I/O
        __u64 stx_attributes;       // Extra file attribute indicators
        __u32 stx_nlink;            // Number of hard links
        __u32 stx_uid;              // User ID of owner
        __u32 stx_gid;              // Group ID of owner
        __u16 stx_mode;             // File type and mode
        __u64 stx_ino;              // Inode number
        __u64 stx_size;             // Total size in bytes
        __u64 stx_blocks;           // Number of 512B blocks allocated
        __u64 stx_attributes_mask;  // Mask to show what's supported in stx_attributes

        // The following fields are file timestamps
        struct statx_timestamp stx_atime;  // Last access
        struct statx_timestamp stx_btime;  // Creation
        struct statx_timestamp stx_ctime;  // Last status change
        struct statx_timestamp stx_mtime;  // Last modification

        // If this file represents a device, then the next two fields contain the ID of the device
        __u32 stx_rdev_major;  // Major ID
        __u32 stx_rdev_minor;  // Minor ID

        // The next two fields contain the ID of the device containing the filesystem where the file resides
        __u32 stx_dev_major;   // Major ID
        __u32 stx_dev_minor;   // Minor ID
    };

    The file timestamps are structures of the following type:

    struct statx_timestamp {
        __s64 tv_sec;    // Seconds since the Epoch (UNIX time)
        __u32 tv_nsec;   // Nanoseconds since tv_sec
    };
*/
