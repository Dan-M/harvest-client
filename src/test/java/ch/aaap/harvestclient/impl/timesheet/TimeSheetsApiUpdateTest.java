package ch.aaap.harvestclient.impl.timesheet;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

import ch.aaap.harvestclient.HarvestTest;
import ch.aaap.harvestclient.api.TimesheetsApi;
import ch.aaap.harvestclient.api.filter.TimeEntryFilter;
import ch.aaap.harvestclient.domain.Project;
import ch.aaap.harvestclient.domain.Task;
import ch.aaap.harvestclient.domain.TimeEntry;
import ch.aaap.harvestclient.domain.User;
import ch.aaap.harvestclient.domain.param.ImmutableTimeEntryCreationInfoDuration;
import ch.aaap.harvestclient.domain.param.ImmutableTimeEntryUpdateInfo;
import ch.aaap.harvestclient.domain.param.TimeEntryCreationInfoDuration;
import ch.aaap.harvestclient.domain.param.TimeEntryUpdateInfo;
import ch.aaap.harvestclient.domain.reference.Reference;
import util.ExistingData;
import util.TestSetupUtil;

@HarvestTest
class TimeSheetsApiUpdateTest {

    private static final Logger log = LoggerFactory.getLogger(TimeSheetsApiUpdateTest.class);

    private static final TimesheetsApi api = TestSetupUtil.getAdminAccess().timesheets();
    private static final Reference<Project> project = ExistingData.getInstance().getProjectReference();
    private static final Reference<User> user = ExistingData.getInstance().getUserReference();
    private static final Reference<Task> task = ExistingData.getInstance().getTaskReference();

    private static TimeEntry entry;

    @BeforeEach
    void beforeEach(TestInfo testInfo) {

        log.debug("Creating entry for test " + testInfo.getDisplayName());

        LocalDate date = LocalDate.now();
        String notes = "Timeentry created for " + testInfo.getDisplayName();

        TimeEntryCreationInfoDuration creationInfo = ImmutableTimeEntryCreationInfoDuration.builder()
                .projectReference(project)
                .taskReference(task)
                .spentDate(date)
                .userReference(user)
                .notes(notes)
                .hours(1.)
                .build();

        entry = api.create(creationInfo);
    }

    @AfterEach
    void afterEach() {

        if (entry != null) {
            api.delete(entry);
        }
    }

    @Test
    void testChangeDuration() {

        TimeEntryUpdateInfo updateInfo = ImmutableTimeEntryUpdateInfo.builder()
                .hours(3.)
                .build();
        TimeEntry updatedEntry = api.update(entry, updateInfo);

        assertThat(updatedEntry.getHours()).isEqualTo(3.);
    }

    @Test
    void testChangeNotes() {

        String newNotes = "This is an updated note";
        TimeEntryUpdateInfo updateInfo = ImmutableTimeEntryUpdateInfo.builder()
                .notes(newNotes)
                .build();
        TimeEntry updatedEntry = api.update(entry, updateInfo);

        assertThat(updatedEntry.getNotes()).isEqualTo(newNotes);
    }

    @Test
    void testChangeAll() {

        String newNotes = "This is an updated note";
        double hours = 5.;
        LocalDate spentDate = LocalDate.of(2018, 2, 2);

        TimeEntryUpdateInfo updateInfo = ImmutableTimeEntryUpdateInfo.builder()
                .hours(hours)
                .notes(newNotes)
                .spentDate(spentDate)
                .build();

        TimeEntry updatedEntry = api.update(entry, updateInfo);

        assertThat(updatedEntry.getNotes()).isEqualTo(newNotes);
        assertThat(updatedEntry.getHours()).isEqualTo(hours);
        assertThat(updatedEntry.getSpentDate()).isEqualTo(spentDate);

    }

    @Test
    void testStopAndRestart() {

        assertThat(entry.getRunning()).isFalse();

        entry = api.restart(entry);

        assertThat(entry.getRunning()).isTrue();

        entry = api.stop(entry);

        assertThat(entry.getRunning()).isFalse();

    }

    /**
     * Convenience method. Delete all timeentries with spent_date is today or later.
     * Uncomment the Test annotation to run
     */
    // @Test
    void deleteAllAfter() {

        TimeEntryFilter filter = new TimeEntryFilter();
        filter.setFrom(LocalDate.of(2018, 2, 1));

        List<TimeEntry> list = api.list(filter);

        list.forEach(api::delete);
        // no need to double delete
        entry = null;
    }
}
