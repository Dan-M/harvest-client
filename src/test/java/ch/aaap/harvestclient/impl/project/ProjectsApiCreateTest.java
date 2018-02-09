package ch.aaap.harvestclient.impl.project;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import ch.aaap.harvestclient.HarvestTest;
import ch.aaap.harvestclient.api.ProjectsApi;
import ch.aaap.harvestclient.domain.Client;
import ch.aaap.harvestclient.domain.Project;
import ch.aaap.harvestclient.domain.param.ProjectCreationInfo;
import ch.aaap.harvestclient.domain.reference.Reference;
import util.ExistingData;
import util.TestSetupUtil;

@HarvestTest
public class ProjectsApiCreateTest {

    private static ProjectsApi projectsApi = TestSetupUtil.getAdminAccess().projects();

    /**
     * Project that get deleted automatically after each test
     */
    private Project project;

    @AfterEach
    void afterEach() {
        if (project != null) {
            projectsApi.delete(project);
            project = null;
        }
    }

    @ParameterizedTest
    @EnumSource(Project.BillingMethod.class)
    void createDefaultBilling(Project.BillingMethod billingMethod, TestInfo testInfo) {

        Reference<Client> clientReference = ExistingData.getInstance().getClient();
        String name = "Project for test " + testInfo.getDisplayName();
        boolean billable = true;
        Project.BillingMethod billBy = billingMethod;
        Project.BudgetMethod budgetBy = Project.BudgetMethod.HOURS_PER_PROJECT;

        ProjectCreationInfo creationInfo = new ProjectCreationInfo(clientReference, name, billable, billBy, budgetBy);
        project = projectsApi.create(creationInfo);

        assertThat(project.getBillable()).isEqualTo(billable);
        assertThat(project.getBillBy()).isEqualTo(billBy);
        assertThat(project.getBudgetBy()).isEqualTo(budgetBy);
        assertThat(project.getName()).isEqualTo(name);
        assertThat(project.getClientReference().getId()).isEqualTo(clientReference.getId());
    }

    @ParameterizedTest
    @EnumSource(Project.BudgetMethod.class)
    void createDefaultBudget(Project.BudgetMethod budgetMethod, TestInfo testInfo) {

        Reference<Client> clientReference = ExistingData.getInstance().getClient();
        String name = "Project for test " + testInfo.getDisplayName();
        boolean billable = true;
        Project.BillingMethod billBy = Project.BillingMethod.PROJECT;
        Project.BudgetMethod budgetBy = budgetMethod;

        ProjectCreationInfo creationInfo = new ProjectCreationInfo(clientReference, name, billable, billBy, budgetBy);
        project = projectsApi.create(creationInfo);

        assertThat(project.getBillable()).isEqualTo(billable);
        assertThat(project.getBillBy()).isEqualTo(billBy);
        assertThat(project.getBudgetBy()).isEqualTo(budgetBy);
        assertThat(project.getName()).isEqualTo(name);
        assertThat(project.getClientReference().getId()).isEqualTo(clientReference.getId());
    }

    @Test
    void createAllOptions(TestInfo testInfo) {

        Reference<Client> clientReference = ExistingData.getInstance().getClient();
        String name = "Project for test " + testInfo.getDisplayName();
        boolean billable = true;
        Project.BillingMethod billBy = Project.BillingMethod.PROJECT;
        Project.BudgetMethod budgetBy = Project.BudgetMethod.HOURS_PER_PROJECT;

        String code = "testCode";
        boolean active = false;
        // see other test for fixedFee = true
        boolean fixedFee = false;
        double hourlyRate = 240;
        double budget = 120;
        boolean notifyWhenOverBudget = true;
        double overBudgetNotificationPercentage = 90.;
        boolean showBudgetToAll = true;
        double costBudget = 2000;
        boolean costBudgetIncludeExpenses = true;
        double fee = 5000;
        String notes = "test notes";
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(3).plusDays(2);

        ProjectCreationInfo creationInfo = new ProjectCreationInfo(clientReference, name, billable, billBy, budgetBy);
        creationInfo.setCode(code);
        creationInfo.setActive(active);
        creationInfo.setFixedFee(fixedFee);
        creationInfo.setHourlyRate(hourlyRate);
        creationInfo.setBudget(budget);
        creationInfo.setNotifyWhenOverBudget(notifyWhenOverBudget);
        creationInfo.setOverBudgetNotificationPercentage(overBudgetNotificationPercentage);
        creationInfo.setShowBudgetToAll(showBudgetToAll);
        creationInfo.setCostBudget(costBudget);
        creationInfo.setCostBudgetIncludeExpenses(costBudgetIncludeExpenses);
        creationInfo.setFee(fee);
        creationInfo.setNotes(notes);
        creationInfo.setStartsOn(start);
        creationInfo.setEndsOn(end);

        project = projectsApi.create(creationInfo);

        assertThat(project).isEqualToIgnoringGivenFields(creationInfo, "clientId", "id", "createdAt", "updatedAt",
                "clientReference", "fee");
        // fee can only be set by having fixed_fee = true
        assertThat(project.getFee()).isNull();
        assertThat(project.getClientReference().getId()).isEqualTo(clientReference.getId());
        assertThat(project.getCreatedAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.MINUTES));
        assertThat(project.getUpdatedAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.MINUTES));

        // get test
        Project gottenProject = projectsApi.get(project);

        assertThat(gottenProject).isEqualToComparingFieldByField(project);

    }

    @Test
    void createFixedFee(TestInfo testInfo) {

        Reference<Client> clientReference = ExistingData.getInstance().getClient();
        String name = "Project for test " + testInfo.getDisplayName();
        boolean billable = true;
        Project.BillingMethod billBy = Project.BillingMethod.TASKS;
        Project.BudgetMethod budgetBy = Project.BudgetMethod.HOURS_PER_PROJECT;

        String code = "testCode";
        boolean active = false;
        boolean fixedFee = true;
        double hourlyRate = 240;
        double budget = 120;
        boolean notifyWhenOverBudget = true;
        double overBudgetNotificationPercentage = 90.;
        boolean showBudgetToAll = true;
        double costBudget = 2000;
        boolean costBudgetIncludeExpenses = true;
        double fee = 5000;
        String notes = "test notes";
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(3).plusDays(2);

        ProjectCreationInfo creationInfo = new ProjectCreationInfo(clientReference, name, billable, billBy, budgetBy);
        creationInfo.setCode(code);
        creationInfo.setActive(active);
        creationInfo.setFixedFee(fixedFee);
        creationInfo.setHourlyRate(hourlyRate);
        creationInfo.setBudget(budget);
        creationInfo.setNotifyWhenOverBudget(notifyWhenOverBudget);
        creationInfo.setOverBudgetNotificationPercentage(overBudgetNotificationPercentage);
        creationInfo.setShowBudgetToAll(showBudgetToAll);
        creationInfo.setCostBudget(costBudget);
        creationInfo.setCostBudgetIncludeExpenses(costBudgetIncludeExpenses);
        creationInfo.setFee(fee);
        creationInfo.setNotes(notes);
        creationInfo.setStartsOn(start);
        creationInfo.setEndsOn(end);

        project = projectsApi.create(creationInfo);

        assertThat(project).isEqualToIgnoringGivenFields(creationInfo, "clientId", "id", "createdAt", "updatedAt",
                "clientReference", "billBy");

        // setting fixed_fee to true changes the billing method to None
        assertThat(project.getBillBy()).isEqualTo(Project.BillingMethod.NONE);
        assertThat(project.getClientReference().getId()).isEqualTo(clientReference.getId());
    }
}