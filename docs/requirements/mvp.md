# HabitForge v1 — MVP Requirements and Business Rules

**Document status:** Draft — Pending Review  
**Version:** 0.1  
**Purpose:** Define the initial product scope, business rules, and API requirements before implementation begins.

---

## 1. Overview

HabitForge is a habit-tracking application that helps users create habits, understand which habits are scheduled for a particular day, and record their completion over time.

The first version (v1 MVP) will focus on the core habit-management and daily-tracking workflows. The objective is to build a small, working application with clearly defined business rules before expanding its features.

The backend is intended to be developed using Java and Spring Boot, with a microservices-based architecture explored as part of the project.

The exact service boundaries, authentication approach, database choices, and deployment strategy remain subject to explicit architecture decisions.

## 2. Goals and Success Criteria

### 2.1 Goals

The v1 MVP should allow a user to:

1. Create a habit with a name and basic details.
2. View their existing habits.
3. Update a habit's details.
4. Define the days on which a habit is scheduled.
5. View habits scheduled for a particular date.
6. Mark a scheduled habit as completed.
7. Correct or undo a recorded completion, subject to the final business rules.
8. View completion history for a habit.
9. Archive a habit without losing its historical completion records.

### 2.2 MVP success criteria

The MVP will be considered functionally successful when the core workflows can be demonstrated end to end:

- A user creates a habit.
- The habit appears on the appropriate scheduled dates.
- The user records a completion for a date.
- The completion is reflected in the habit's history.
- Repeated requests do not create duplicate completion records.
- Archiving a habit preserves its previous completion history.

These criteria describe the intended product behavior. Detailed acceptance tests will be finalized after the open questions in this document are reviewed.

## 3. Scope

### 3.1 In scope

The following capabilities are proposed for v1:

- Basic habit creation, retrieval, and updating.
- Habit scheduling for every day or selected weekdays.
- Retrieval of habits scheduled for a specified date.
- Recording habit completion for a business date.
- Viewing completion history for a habit.
- Handling duplicate completion requests safely.
- Correcting or undoing completion records according to an agreed policy.
- Archiving habits while preserving historical records.
- Input validation and meaningful API error responses.
- Automated tests for core business rules.
- Service-to-service communication where required by the agreed architecture.
- Authentication and user ownership, using an approach to be decided before implementation.

### 3.2 Out of scope for the initial MVP

The following features are excluded unless explicitly added through a later scope decision:

- Social features, friends, and public habit sharing.
- Leaderboards and community challenges.
- Gamification, points, badges, and rewards.
- AI-generated habit recommendations.
- Reminders, push notifications, SMS, and email notifications.
- Advanced analytics, charts, and predictive insights.
- Paid subscriptions and billing.
- Calendar integrations.
- Offline synchronization.
- Multiple organization or team workspaces.
- Complex recurring schedules, such as alternating weeks or custom recurrence expressions.
- Production-scale infrastructure and multi-region deployment.

The exclusion of a feature from this list does not prevent it from being considered for a future version.

## 4. Core User Workflows

### 4.1 Create a habit

1. The user submits a request to create a habit.
2. The system validates the required fields.
3. The system stores the habit.
4. The system returns the created habit and its identifier.

**Expected result:** The habit can subsequently be retrieved and appears on dates matching its schedule.

### 4.2 View and update habits

1. The user requests their habits.
2. The system returns the habits the user is authorized to access.
3. The user selects a habit to update.
4. The system validates and stores the changes.
5. Subsequent retrievals return the updated details.

**Expected result:** A user cannot modify another user's habit.

### 4.3 View habits scheduled for a date

1. The user requests the habits scheduled for a particular date.
2. The system evaluates each active habit's schedule for that date.
3. The system returns the matching habits and their completion state, where applicable.

**Expected result:** A habit appears only on dates allowed by its schedule and lifecycle state.

### 4.4 Complete a habit

1. The user selects a scheduled habit and a business date.
2. The system validates that the habit exists and that the user is authorized to access it.
3. The system validates the completion request against the agreed date and scheduling rules.
4. The system records the completion.
5. The system returns the completion result.

**Expected result:** The habit is represented as completed for that date, and repeated equivalent requests do not create additional completion records.

### 4.5 View completion history

1. The user requests completion history for a habit.
2. The system returns the available completion records for the requested period or the full history, depending on the API contract.
3. Previously recorded history remains available even if the habit has been archived.

**Expected result:** The user can review past completions without losing history due to habit archival.

### 4.6 Archive a habit

1. The user requests to archive a habit.
2. The system validates ownership and the habit's current state.
3. The system marks the habit as archived.
4. The habit is excluded from normal active-habit views and future scheduling, according to the final archival policy.
5. Historical completion records remain preserved.

**Expected result:** Archiving is a lifecycle change, not an instruction to delete the habit's history.

## 5. Habit Business Rules

The following rules are proposed for review. A rule marked **Proposed** is not an approved decision until it is explicitly accepted.

### HR-01: Required habit name

**Proposed rule:** Every habit must have a non-empty name after trimming leading and trailing whitespace.

The final maximum name length and any additional text restrictions remain to be decided.

### HR-02: Habit ownership

**Proposed rule:** Each habit belongs to one user. Only an authorized owner can view, update, or archive that habit.

The authentication mechanism and the way user identity is propagated between services remain unresolved.

### HR-03: Habit schedule

**Proposed rule:** A habit supports one of the following schedules:

- Daily: scheduled every calendar day in the applicable business timezone.
- Selected weekdays: scheduled on one or more selected weekdays.

A selected-weekdays schedule must contain at least one valid weekday.

More complex recurrence patterns are excluded from v1.

### HR-04: Habit status

**Proposed rule:** A habit has a lifecycle status of active or archived.

Active habits can be managed and scheduled according to their configured rules. Archived habits are excluded from normal active-habit views.

Whether an archived habit can be restored and how archival affects historical-date completion requests remain open questions.

### HR-05: Habit updates

**Proposed rule:** Updating a habit changes its current details without rewriting its historical completion records.

The effect of changing a schedule on dates before and after the update must be defined before implementation.

### HR-06: Habit archival

**Proposed rule:** Archiving a habit must not physically delete its existing completion history.

Whether archived habits can be unarchived is unresolved.

## 6. Completion Business Rules

### CR-01: Completion identity

**Proposed rule:** A completion record represents the completion of one habit for one business date.

The intended uniqueness constraint is:

`(habitId, businessDate)`

This assumes the v1 product allows at most one completion record per habit per business date.

### CR-02: Duplicate completion requests

**Proposed rule:** Repeating a request to complete the same habit for the same business date must not create a second completion record.

The API must return a documented, consistent result for repeated requests. Whether this returns the existing completion as a successful response or a conflict response is unresolved.

### CR-03: Completion of past dates

**Unresolved decision:** The system must define whether users may complete a habit for a past date.

Questions to resolve:

- Are past-date completions allowed?
- If allowed, how far back can a user record completion?
- Does the habit need to have been active and scheduled on that historical date?
- What happens if the habit was created or its schedule changed after that date?

Until these rules are agreed, past-date completion must not be treated as unrestricted.

### CR-04: Completion of future dates

**Unresolved decision:** Decide whether future-date completions are prohibited or allowed under specific conditions.

A reasonable proposed default is to reject future-date completion requests, but this requires approval.

### CR-05: Undo or correction

**Unresolved decision:** Define how an existing completion can be undone or corrected.

Possible approaches include:

- Deleting the completion record.
- Retaining the record with a cancelled or reversed state.
- Supporting a separate correction operation with an audit trail.

The selected approach should be consistent with the v1 requirement for reliable completion history.

### CR-06: Completion scheduling validation

**Proposed rule:** A completion request must be validated against the habit's schedule and lifecycle rules.

Whether a habit that is now archived can receive a completion for a date when it was active remains unresolved.

### CR-07: Completion history preservation

**Proposed rule:** Updating or archiving a habit must not silently remove its existing completion history.

Any future correction or deletion behavior must be explicitly defined and tested.

### CR-08: Concurrent completion requests

**Proposed rule:** Concurrent requests for the same habit and business date must not create duplicate completion records.

The implementation should enforce the agreed uniqueness rule at the persistence layer rather than relying exclusively on application-level checks.

## 7. Business Date and Timezone Rules

### TZ-01: Business date

A completion is associated with a business date, represented as a date without a time component, rather than being identified solely by the server's current timestamp.

The proposed API representation is ISO 8601 date format:

`YYYY-MM-DD`

Example: `2026-10-17`.

### TZ-02: Timezone handling

**Unresolved decision:** Determine whether each user has a configured IANA timezone, such as `Asia/Kolkata`, or whether v1 uses a single configured application timezone.

The system must not accidentally derive a user's business date from the server timezone when that differs from the agreed business timezone.

### TZ-03: Historical date interpretation

The system must define how a habit's schedule is evaluated for a historical date, especially when the schedule, timezone, or lifecycle state has changed.

### TZ-04: Timestamps

**Proposed rule:** Creation and update timestamps should be stored consistently, preferably as UTC instants.

Business dates and event timestamps serve different purposes and must not be treated as interchangeable.

## 8. Initial API Requirements

The following endpoints are a proposed starting point for the first vertical slice. They are not a finalized API contract. Request bodies, response schemas, validation rules, authentication requirements, and error formats will be refined during design.

### 8.1 Habit Service

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/v1/habits` | Create a habit |
| `GET` | `/api/v1/habits` | List the current user's habits |
| `GET` | `/api/v1/habits/{habitId}` | Retrieve one habit |
| `PUT` | `/api/v1/habits/{habitId}` | Update a habit |
| `POST` | `/api/v1/habits/{habitId}/archive` | Archive a habit |
| `GET` | `/api/v1/habits?date={YYYY-MM-DD}` | Retrieve habits scheduled for a date |

**Notes:**

- The date-filtered listing is a proposed API shape; its final contract may use a dedicated endpoint or query parameter.
- User identity must come from a trusted authentication context, not from an arbitrary client-supplied owner identifier.
- The behavior for archived habits and schedule changes must follow the agreed business rules.

### 8.2 Tracking Service

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/v1/habits/{habitId}/completions` | Record completion for a habit and business date |
| `GET` | `/api/v1/habits/{habitId}/completions` | Retrieve completion history for a habit |
| `GET` | `/api/v1/completions?date={YYYY-MM-DD}` | Retrieve completions for a business date |
| `DELETE` | `/api/v1/habits/{habitId}/completions/{businessDate}` | Proposed option for undoing a completion |

**Notes:**

- The delete endpoint is only a candidate until the undo/correction policy is approved.
- The completion request should identify the business date explicitly.
- Duplicate handling and concurrent-request behavior must be documented in the API contract.
- The ownership-validation and cross-service communication approach remains to be decided.

### 8.3 Illustrative completion request

Proposed request:

`POST /api/v1/habits/{habitId}/completions`

Example JSON body:

```json
{
  "businessDate": "2026-10-17"
}
```

This is an illustrative contract, not a finalized schema.

## 9. Proposed Service Responsibilities

### 9.1 Habit Service

Proposed responsibilities:

- Create and maintain habit details.
- Validate habit names and schedules.
- Manage active and archived status.
- Provide habit details and scheduling information.
- Expose APIs for authorized habit management.

### 9.2 Tracking Service

Proposed responsibilities:

- Record habit completion.
- Enforce completion uniqueness.
- Provide completion history.
- Apply agreed completion-date and correction rules.
- Coordinate with the Habit Service when habit existence, schedule, status, or ownership information is needed.

### 9.3 Service boundary questions

The following remain unresolved:

- Which service owns the authoritative user identity and ownership checks?
- How should the Tracking Service obtain habit schedule and status information?
- Should the initial implementation use synchronous HTTP communication between services?
- How should service failures and partial failures be handled?
- Is a separate API gateway required for the first working version?

These questions must be reviewed before they become implementation commitments.

## 10. Authentication and Authorization

**Status: Unresolved architecture decision**

Two candidate approaches are under consideration.

### Option A: Keycloak

Use Keycloak as an identity and access management solution to handle authentication and token issuance.

Potential benefits:

- Avoid building a complete authentication system from scratch.
- Learn standard token-based authentication and authorization integration.
- Keep identity management separate from business services.

Potential trade-offs:

- Additional infrastructure and configuration.
- More concepts to learn before the first business workflow is complete.

### Option B: Custom authentication

Implement authentication functionality within the application.

Potential benefits:

- Direct control over the initial implementation.
- Potentially fewer external components for a small learning prototype.

Potential trade-offs:

- Password handling, token management, account security, and other responsibilities must be designed and implemented safely.
- Greater risk of creating an incomplete security implementation.

### Decision required

Choose the authentication approach before implementing security-sensitive endpoints.

The choice must be recorded as an explicit architecture decision. Neither option is approved by this document.

Regardless of the selected option, v1 must prevent users from accessing or modifying other users' habits and completion data.

## 11. Validation and Error Handling

The APIs should return consistent, meaningful error responses for at least the following cases:

- Missing or invalid required fields.
- Invalid weekday values or an empty selected-weekdays schedule.
- Habit not found.
- User not authorized to access the requested resource.
- Completion request violates the agreed scheduling or date policy.
- Duplicate completion request.
- Attempt to perform an unsupported operation on an archived habit.
- Unexpected service or persistence failure.

**Proposed rule:** Error responses should include an appropriate HTTP status code and a machine-readable error identifier.

The exact error response schema and status-code mapping remain to be finalized during API design.

## 12. Testing Requirements

The first implementation should include automated tests for the following scenarios.

### Habit management

- A valid habit can be created.
- A blank habit name is rejected.
- A daily habit is scheduled every applicable day.
- A selected-weekdays habit is scheduled only on the configured weekdays.
- An invalid or empty weekday selection is rejected.
- Updating a habit preserves existing completion history.
- Archiving a habit preserves existing completion history.

### Completion tracking

- A valid completion can be recorded.
- A duplicate completion does not create a second record.
- Concurrent duplicate requests do not create duplicate records.
- Completion history can be retrieved.
- Invalid completion dates are rejected according to the agreed policy.
- Undo or correction behaves according to the approved design.
- Archived-habit completion requests follow the agreed historical-date policy.

### Authorization and integration

- A user cannot access another user's habit.
- A user cannot read or modify another user's completion records.
- Tracking behavior remains consistent when the Habit Service is unavailable.
- Cross-service failures produce documented responses.

Some test expectations depend on decisions still open in this document. Those tests should be finalized once the corresponding rules are agreed.

## 13. Assumptions

The following are working assumptions for planning and review, not automatically approved requirements:

1. HabitForge v1 focuses on individual habit management and tracking.
2. Daily and selected-weekdays schedules are sufficient for the first version.
3. Each habit belongs to one user.
4. A habit can have at most one completion record per business date.
5. Completion history should survive habit updates and archival.
6. Habit Service and Tracking Service are candidate service boundaries.
7. Java and Spring Boot are the intended backend technologies.
8. The initial API uses a versioned `/api/v1` path.
9. Authentication and authorization are required, but their implementation approach is undecided.
10. The project should prioritize one complete, testable vertical slice before adding advanced features.

## 14. Open Questions and Decisions Required

The following questions must be resolved or explicitly deferred before the relevant functionality is implemented.

| ID | Question | Status |
|---|---|---|
| D-01 | Keycloak or custom authentication? | Unresolved |
| D-02 | Is the business timezone configured per user or globally? | Unresolved |
| D-03 | Are past-date completions allowed, and if so, how far back? | Unresolved |
| D-04 | Are future-date completions prohibited? | Unresolved |
| D-05 | How should a completion be undone or corrected? | Unresolved |
| D-06 | What happens to historical scheduling when a habit's schedule changes? | Unresolved |
| D-07 | Can an archived habit be restored? | Unresolved |
| D-08 | Can an archived habit receive a completion for a historical date? | Unresolved |
| D-09 | Should a duplicate completion return the existing record or a conflict response? | Unresolved |
| D-10 | How should the Tracking Service validate habit status, schedule, and ownership? | Unresolved |
| D-11 | What API error response format should all services use? | Unresolved |
| D-12 | Is an API gateway needed for the first vertical slice? | Unresolved |

## 15. Definition of Done for the Requirements Phase

This requirements phase is complete when:

- The v1 scope is clearly separated into in-scope and out-of-scope features.
- Habit-management rules are written in testable language.
- Completion, duplicate-request, archival, and history-preservation behavior are documented.
- Business-date and timezone responsibilities are described.
- Initial Habit Service and Tracking Service API methods and paths are listed.
- Proposed decisions are clearly distinguished from approved decisions.
- Unresolved questions are visible and can be discussed explicitly.
- The document is committed to GitHub.
- The document and relevant decisions are reviewed and agreed in the main HabitForge development chat.

No application code or generated UI is part of this task. Implementation should begin only after the requirements document has been reviewed.

---

**End of document**