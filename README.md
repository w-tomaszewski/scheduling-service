# scheduling-service

* A meeting scheduling service that facilitates creating meetings and managing schedules.
* Requirements clarification:
* - Added 'checkConflicts' parameter to createMeeting method to indicate whether to check for conflicts.
* - If conflicts are detected and 'checkConflicts' is set to true, an exception is thrown.
* - Initial data for persons is provided via an in-memory person repository, which sets up some predefined persons and meetings for testing and demonstration purposes.
* Note on time zones:
* - The system's default time zone is used for handling current date and time.
* - LocalDateTime.now() returns the current date and time based on the system clock of the local machine.
* Potential implementation compromises:
* - Some design choices, such as conflict checking and handling of time zones, were made based on assumptions and may not fully meet all requirements or edge cases.
* - Implementing support for time zones might require refactoring to use OffsetDateTime instead.
* - The error handling and validation mechanisms covers basic scenarios and could be improved f.e. if an exception occurs during data initialization,
*   the entire service is marked as failed.
* - The current implementation may not scale well for a large number of persons or meetings but the code could be refactored to be safe for multiple threads
*   by replace HashMap with ConcurrentHashMap, synchronize methods or critical sections.
    */# scheduling-service
