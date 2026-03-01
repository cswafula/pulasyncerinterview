# Architecture Documentation Pula Survey Sync Engine

## 1. Architectural Design

The sync engine follows a **layered clean architecture** with three distinct layers:

- **Domain**. Holds the business models, repository interfaces, error contracts.
- **Data**. Holds Room entities, DAOs, repository implementations, remote DTOs, and the
  sync engine itself. Depends inward on domain interfaces.
- **App**. Hilt DI wiring only. Nothing here.

Dependency injection is handled by **Hilt** to keep construction logic out of business
logic and to make every component independently testable. Async operations use
**Kotlin Coroutines** throughout `suspend` functions for one-shot operations, a
`Mutex` for concurrency control.

## 2. Media File Uploads | Photo Compression Strategy (Not Implemented)

Photo compression must happen **before** the upload payload is assembled, not inside
the sync engine loop. The reason is separation of concerns; I think the sync engine should
NOT know about file formats.

Images could be converted to webp format to reduce the payload size as opposed to jpeg file format.

The proposed extension:

1. Add a `CompressionWorker` (WorkManager) that runs as a pre-processing step before
   `SyncWorker`. It queries all `AttachmentEntity` records where `isSynced = false`,
   compresses each photo to a target of ~200KB using `Bitmap` downsampling, writes
   the compressed file to a temp path, and updates `localUri` on the entity to point
   to the compressed file.
2. `SyncWorker` then chains after `CompressionWorker` using WorkManager's
   `beginWith(compress).then(sync)` chaining. This guarantees compression always
   precedes upload.
3. After a successful upload, both the compressed temp file and the original are
   deleted from disk. The `AttachmentEntity` row is also deleted.

This keeps the sync engine unchanged and makes compression independently testable.

----

## 3. Storage Management Approach

Per the description, we expect Field agents to collect upto 50+ surveys per day with photo attachments on 16–32 GB devices.
Immediate risk here, whereby without active management, local storage fills within days.

**Two-tier proofing:**

- **Photo deletion** : attachments are deleted immediately after each successful
  response upload. Photos are the largest storage consumer and have no value once
  synced. This assumes the app does not hold a feature to display uploaded surveys including atachments.
- **N Old Survey Answers purging(Not implemented)**: after every sync session, we implement a`StorageManager` which checks available
  device storage. If it drops below 250 MB, the oldest N synced `SurveyResponse` records
  are purged from Room DB, keeping the say 50 most recent. The 50-record buffer preserves
  recent history for field supervisor spot checks without accumulating unbounded data.

Additional measure: Critical threshold (100 MB) is also tracked by the `StorageManager` whereby the app should warn the
agent and stop accepting new photo attachments entirely.

----

## 4. Remote Troubleshooting | Custom Request Tagging Feature

If a field agent's sync is failing and the support team cannot physically access the
device, the following data would be sufficient to diagnose most issues:

This works on the assumtion there is an existing escallation matrix used by the team on ground and L1 support team.

**What to can be logged on device (structured, queryable):**

- Sync session start/end timestamps
- Number of pending responses at session start
- Per-response outcome: responseId, each state machine status transition, error code (e.g. `UPLD_400`)
- Device Android API level and available RAM at session start

**Request Tagging:**

- The `RequestTagConfig` creates an error mapping feature which error codes (`UPLD_400`) appear in the local
  log, making it immediately clear which endpoint is rejecting requests without
  needing to reproduce the issue. The Config maps a url path to a code identifiable by the development team.

----

## 5. GPS Field Boundary Capture | Anticipated Challenges

I would anticipate the following challenges are anticipated:

**Accuracy under covers and poor satellite geometry:**
Rural Sub-Saharan Africa has significant tree canopy in farming areas. Android's
`LocationManager` fused provider blends GPS, WiFi, and cell tower signals. In areas
with no WiFi or cell coverage, only raw GPS is available, accuracy can drift alot.

**Validation strategy:**
- Implement a location tracking strategy that relies on two locationListeners; GPS and location as the fallback plan.
- Cross-check GPS-captured boundaries against the farmer's self-reported farm size
  answer in the survey. A large discrepancy triggers a soft warning, not a hard block.
- Store raw coordinates locally and sync them as part of the survey answer JSON blob.

----

## 7. One Thing I Would Do Differently With More Time

**Implement the work managers, storageManager & Display UIs.**

- Missing in my implementation are the work managers integration, storageManager to observe user device storage and implement right processes & the UI implementations to complement my submission.

```