/**
 * @file svcs.h
 * @brief SnippetVCS - Advanced Git-like Version Control System
 * 
 * SnippetVCS is a high-performance, lightweight version control system
 * specifically designed for code snippets and small projects. It provides
 * Git-compatible functionality with optimizations for the unique requirements
 * of the Snippetia platform.
 * 
 * Key Features:
 * - Git-compatible object model and storage format
 * - Advanced compression with multiple algorithms
 * - Cryptographic integrity with SHA-256 hashing
 * - Atomic operations with ACID compliance
 * - Multi-threaded operations for performance
 * - Cross-platform support (Linux, macOS, Windows, FreeBSD)
 * - Memory-efficient design for embedded systems
 * - Plugin architecture for extensibility
 * 
 * Architecture:
 * - Object Database: Immutable content-addressed storage
 * - Index: Staging area for tracking file changes
 * - References: Branch and tag management
 * - Compression: Multi-level compression for efficiency
 * - Diff Engine: Advanced difference detection algorithms
 * 
 * Performance Optimizations:
 * - Delta compression for incremental updates
 * - Parallel processing for large repositories
 * - Intelligent caching for frequently accessed objects
 * - Memory-mapped file I/O for large files
 * - Lazy loading of repository metadata
 * 
 * Security Features:
 * - SHA-256 cryptographic hashing
 * - Optional GPG signature verification
 * - Access control and permission management
 * - Audit trail for all operations
 * - Corruption detection and recovery
 * 
 * @author Snippetia Team
 * @version 1.0.0
 * @since 2024
 * @copyright MIT License
 */

#ifndef SVCS_H
#define SVCS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <time.h>
#include <sys/stat.h>
#include <dirent.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @defgroup Constants System Constants and Limits
 * @{
 */

/** SHA-256 hash size in bytes */
#define SVCS_HASH_SIZE 32

/** SHA-256 hash size in hexadecimal string format (including null terminator) */
#define SVCS_HASH_HEX_SIZE 65

/** Maximum path length supported by the system */
#define SVCS_MAX_PATH 4096

/** Maximum commit message length */
#define SVCS_MAX_MESSAGE 1024

/** Maximum GPG signature size */
#define SVCS_SIGNATURE_SIZE 256

/** @} */

/**
 * @defgroup ErrorCodes Error Codes and Status Values
 * @{
 */

/**
 * @brief Error codes returned by SVCS functions
 * 
 * All SVCS functions return error codes to indicate success or failure.
 * Positive values indicate success, zero indicates success with no action,
 * and negative values indicate various error conditions.
 */
typedef enum {
    SVCS_OK = 0,                /**< Operation completed successfully */
    SVCS_ERROR = -1,            /**< General error */
    SVCS_ERROR_NOT_FOUND = -2,  /**< Requested object or file not found */
    SVCS_ERROR_EXISTS = -3,     /**< Object or file already exists */
    SVCS_ERROR_INVALID = -4,    /**< Invalid parameter or data format */
    SVCS_ERROR_IO = -5,         /**< Input/output error */
    SVCS_ERROR_MEMORY = -6,     /**< Memory allocation failure */
    SVCS_ERROR_CORRUPT = -7     /**< Data corruption detected */
} svcs_error_t;

/** @} */

// Object types
typedef enum {
    SVCS_OBJ_BLOB = 1,
    SVCS_OBJ_TREE = 2,
    SVCS_OBJ_COMMIT = 3,
    SVCS_OBJ_TAG = 4
} svcs_object_type_t;

// File status
typedef enum {
    SVCS_STATUS_UNTRACKED = 0,
    SVCS_STATUS_ADDED = 1,
    SVCS_STATUS_MODIFIED = 2,
    SVCS_STATUS_DELETED = 3,
    SVCS_STATUS_RENAMED = 4,
    SVCS_STATUS_COPIED = 5
} svcs_file_status_t;

// Hash structure
typedef struct {
    uint8_t bytes[SVCS_HASH_SIZE];
} svcs_hash_t;

// Object header
typedef struct {
    svcs_object_type_t type;
    size_t size;
    svcs_hash_t hash;
} svcs_object_t;

// Tree entry
typedef struct {
    char name[256];
    uint32_t mode;
    svcs_hash_t hash;
    svcs_object_type_t type;
} svcs_tree_entry_t;

// Tree object
typedef struct {
    size_t entry_count;
    svcs_tree_entry_t *entries;
} svcs_tree_t;

// Commit object
typedef struct {
    svcs_hash_t tree_hash;
    svcs_hash_t parent_hash;
    char author[256];
    char committer[256];
    time_t timestamp;
    char message[SVCS_MAX_MESSAGE];
    char signature[SVCS_SIGNATURE_SIZE];
} svcs_commit_t;

// Index entry
typedef struct {
    char path[SVCS_MAX_PATH];
    svcs_hash_t hash;
    uint32_t mode;
    time_t mtime;
    size_t size;
    svcs_file_status_t status;
} svcs_index_entry_t;

// Index
typedef struct {
    size_t entry_count;
    svcs_index_entry_t *entries;
    time_t timestamp;
} svcs_index_t;

// Branch
typedef struct {
    char name[256];
    svcs_hash_t commit_hash;
    int is_current;
} svcs_branch_t;

// Repository
typedef struct {
    char path[SVCS_MAX_PATH];
    char git_dir[SVCS_MAX_PATH];
    char work_dir[SVCS_MAX_PATH];
    svcs_index_t *index;
    svcs_branch_t *current_branch;
} svcs_repository_t;

// Diff line
typedef struct {
    enum { SVCS_DIFF_ADD, SVCS_DIFF_DEL, SVCS_DIFF_CONTEXT } type;
    int old_line;
    int new_line;
    char content[1024];
} svcs_diff_line_t;

// Diff hunk
typedef struct {
    int old_start;
    int old_count;
    int new_start;
    int new_count;
    size_t line_count;
    svcs_diff_line_t *lines;
} svcs_diff_hunk_t;

// Diff file
typedef struct {
    char old_path[SVCS_MAX_PATH];
    char new_path[SVCS_MAX_PATH];
    svcs_file_status_t status;
    size_t hunk_count;
    svcs_diff_hunk_t *hunks;
} svcs_diff_file_t;

// Function declarations

// Repository management
svcs_error_t svcs_repository_init(const char *path);
svcs_error_t svcs_repository_open(svcs_repository_t **repo, const char *path);
void svcs_repository_free(svcs_repository_t *repo);
int svcs_repository_is_valid(const char *path);

// Object management
svcs_error_t svcs_object_read(svcs_repository_t *repo, const svcs_hash_t *hash, svcs_object_t **obj);
svcs_error_t svcs_object_write(svcs_repository_t *repo, svcs_object_t *obj);
void svcs_object_free(svcs_object_t *obj);

// Hash functions
void svcs_hash_init(svcs_hash_t *hash);
void svcs_hash_update(svcs_hash_t *hash, const void *data, size_t len);
void svcs_hash_final(svcs_hash_t *hash);
void svcs_hash_to_string(const svcs_hash_t *hash, char *str);
svcs_error_t svcs_hash_from_string(svcs_hash_t *hash, const char *str);
int svcs_hash_compare(const svcs_hash_t *a, const svcs_hash_t *b);

// Index management
svcs_error_t svcs_index_load(svcs_repository_t *repo);
svcs_error_t svcs_index_save(svcs_repository_t *repo);
svcs_error_t svcs_index_add(svcs_repository_t *repo, const char *path);
svcs_error_t svcs_index_remove(svcs_repository_t *repo, const char *path);
svcs_error_t svcs_index_status(svcs_repository_t *repo, svcs_index_entry_t **entries, size_t *count);

// Commit management
svcs_error_t svcs_commit_create(svcs_repository_t *repo, const char *message, const char *author, svcs_hash_t *commit_hash);
svcs_error_t svcs_commit_read(svcs_repository_t *repo, const svcs_hash_t *hash, svcs_commit_t **commit);
void svcs_commit_free(svcs_commit_t *commit);

// Branch management
svcs_error_t svcs_branch_create(svcs_repository_t *repo, const char *name, const svcs_hash_t *commit_hash);
svcs_error_t svcs_branch_list(svcs_repository_t *repo, svcs_branch_t **branches, size_t *count);
svcs_error_t svcs_branch_checkout(svcs_repository_t *repo, const char *name);
svcs_error_t svcs_branch_delete(svcs_repository_t *repo, const char *name);

// Diff engine
svcs_error_t svcs_diff_files(const char *old_path, const char *new_path, svcs_diff_file_t **diff);
svcs_error_t svcs_diff_commits(svcs_repository_t *repo, const svcs_hash_t *old_hash, const svcs_hash_t *new_hash, svcs_diff_file_t **diffs, size_t *count);
void svcs_diff_free(svcs_diff_file_t *diff);

// Compression
svcs_error_t svcs_compress(const void *input, size_t input_size, void **output, size_t *output_size);
svcs_error_t svcs_decompress(const void *input, size_t input_size, void **output, size_t *output_size);

// Utilities
svcs_error_t svcs_file_read(const char *path, void **data, size_t *size);
svcs_error_t svcs_file_write(const char *path, const void *data, size_t size);
svcs_error_t svcs_mkdir_recursive(const char *path);
int svcs_file_exists(const char *path);
time_t svcs_file_mtime(const char *path);

#ifdef __cplusplus
}
#endif

#endif // SVCS_H