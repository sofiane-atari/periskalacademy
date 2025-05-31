package periskal.academy.backend.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import periskal.academy.backend.jpa.Billing;
import periskal.academy.backend.jpa.BillingRepository;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.UserExamAttempt;
import periskal.academy.backend.jpa.UserExamAttemptRepository;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillingRepository billingRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BillingService billingService;

    @Mock
    private UserExamAttemptRepository userExamAttemptRepository;

    @Mock
    private ExamService examService;

    private Billing testBillingEntity;

    @BeforeEach
    void setUp() {
        testBillingEntity = new Billing();
        testBillingEntity.setId(1L);
        testBillingEntity.setFullName("Test Customer");
        testBillingEntity.setExamId("1");
        testBillingEntity.setAddress("test");
        testBillingEntity.setEmail("hallo@test.be");
        testBillingEntity.setTax("120213456");
        testBillingEntity.setZipCode("2000");
        testBillingEntity.setState("Antwerpen");
        testBillingEntity.setCompanyName("Test Ltd.");
        testBillingEntity.setUserId("PER00001");
        testBillingEntity.setCity("Antwerpen");
        testBillingEntity.setCountryCode("BE");
    }

    /**
     * Test case for the successful saving of a Billing entity.
     * Given a valid Billing entity,
     * When saveOrder is called,
     * Then it should return the saved Billing entity and call orderRepository.save
     * once.
     */
    @Test
    void givenValidBillingEntity_whenSaveOrder_thenReturnsSavedBillingAndCallsRepositorySave() {
        // Arrange: Configure the mock repository to return the same entity when save is
        // called
        when(billingRepository.save(any(Billing.class))).thenReturn(testBillingEntity);

        Exam mockExam = new Exam();
        mockExam.setId(1L);
        mockExam.setExamTitleEn("Sample Exam Title");

        when(examService.findById(1L)).thenReturn(Optional.of(mockExam));


        // Act: Call the saveOrder method on the service
        Billing savedBilling = billingService.saveOrder(testBillingEntity);

        assertThat(savedBilling)
                .isNotNull()
                .isEqualTo(testBillingEntity);

        assertThat(savedBilling.getId()).isEqualTo(testBillingEntity.getId());
        assertThat(savedBilling.getFullName()).isEqualTo(testBillingEntity.getFullName());
        assertThat(savedBilling.getAddress()).isEqualTo(testBillingEntity.getAddress());
        assertThat(savedBilling.getCity()).isEqualTo(testBillingEntity.getCity());
        assertThat(savedBilling.getCompanyName()).isEqualTo(testBillingEntity.getCompanyName());
        assertThat(savedBilling.getCountryCode()).isEqualTo(testBillingEntity.getCountryCode());
        assertThat(savedBilling.getEmail()).isEqualTo(testBillingEntity.getEmail());
        assertThat(savedBilling.getExamId()).isEqualTo(testBillingEntity.getExamId());
        assertThat(savedBilling.getState()).isEqualTo(testBillingEntity.getState());
        assertThat(savedBilling.getTax()).isEqualTo(testBillingEntity.getTax());
        assertThat(savedBilling.getUserId()).isEqualTo(testBillingEntity.getUserId());
        assertThat(savedBilling.getZipCode()).isEqualTo(testBillingEntity.getZipCode());

        // Assert that the save method on the repository was called exactly once with
        // the correct entity
        verify(billingRepository, times(1)).save(testBillingEntity);
    }

    /**
     * Test case for when the BillingRepository throws an exception during save
     * operation.
     * Given a valid Billing entity,
     * When saveOrder is called and the repository throws a RuntimeException,
     * Then it should propagate the RuntimeException.
     */
    @Test
    void givenValidBillingEntity_whenRepositorySaveThrowsException_thenThrowsRuntimeException() {
        // Arrange: Configure the mock repository to throw a RuntimeException when save
        // is
        // called
        RuntimeException expectedException = new RuntimeException("Database connection failed");
        when(billingRepository.save(any(Billing.class))).thenThrow(expectedException);

        // Act: Use AssertJ's assertThatThrownBy for asserting exceptions
        assertThatThrownBy(() -> billingService.saveOrder(testBillingEntity))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database connection failed");

        // Assert that the save method on the repository was still attempted once
        verify(billingRepository, times(1)).save(testBillingEntity);
    }

    /**
     * Test case for handling a null Billing entity being passed to saveOrder.
     * Given a null Billing entity,
     * When saveOrder is called,
     * Then it should delegate to the repository, which is expected to throw an
     * IllegalArgumentException.
     */
    @Test
    void givenNullBillingEntity_whenSaveOrder_thenThrowsIllegalArgumentException() {
        // Arrange: A null Billing entity
        Billing nullBilling = null;

        when(billingRepository.save(ArgumentMatchers.isNull()))
                .thenThrow(new IllegalArgumentException("Entity to save must not be null"));

        // Act: Use AssertJ's assertThatThrownBy for asserting exceptions
        assertThatThrownBy(() -> billingService.saveOrder(nullBilling))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity to save must not be null");

        // Assert that the save method on the repository was called once with null
        verify(billingRepository, times(1)).save(ArgumentMatchers.isNull());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenValidBillingEntityList_whenSaveAttemptsBatch_thenReturnsSavedBillingListAndCreatesUserExamAttempts() {
    // Arrange: Create a list of test billing entities
    Billing billing1 = new Billing();
    billing1.setId(1L);
    billing1.setFullName("Customer 1");
    billing1.setExamId("EXAM001");
    billing1.setUserId("USER001");
    billing1.setExamAttempts(3);
    billing1.setEmail("customer1@test.com");

    Billing billing2 = new Billing();
    billing2.setId(2L);
    billing2.setFullName("Customer 2");
    billing2.setExamId("EXAM002");
    billing2.setUserId("USER002");
    billing2.setExamAttempts(5);
    billing2.setEmail("customer2@test.com");

    List<Billing> inputBillingList = Arrays.asList(billing1, billing2);
    List<Billing> savedBillingList = Arrays.asList(billing1, billing2);

    // Configure mocks
    when(billingRepository.saveAll(inputBillingList)).thenReturn(savedBillingList);
    when(userExamAttemptRepository.saveAll(ArgumentMatchers.<UserExamAttempt>anyList())).thenReturn(Collections.<UserExamAttempt>emptyList());

    // Act: Call the saveAttemptsBatch method
    List<Billing> result = billingService.saveAttemptsBatch(inputBillingList);

    // Assert: Verify the returned billing list
    assertThat(result)
            .isNotNull()
            .hasSize(2)
            .containsExactlyElementsOf(savedBillingList);

    // Verify billingRepository.saveAll was called once with correct input
    verify(billingRepository, times(1)).saveAll(inputBillingList);

    // Capture and verify the UserExamAttempt entities that were saved
    ArgumentCaptor<List<UserExamAttempt>> attemptCaptor = ArgumentCaptor.forClass(List.class);
    verify(userExamAttemptRepository, times(1)).saveAll(attemptCaptor.capture());

    List<UserExamAttempt> capturedAttempts = attemptCaptor.getValue();
    assertThat(capturedAttempts)
            .hasSize(2);

    // Verify first UserExamAttempt
    UserExamAttempt attempt1 = capturedAttempts.get(0);
    assertThat(attempt1.getExamId()).isEqualTo("EXAM001");
    assertThat(attempt1.getUserId()).isEqualTo("USER001");
    assertThat(attempt1.getTotalAttempts()).isEqualTo(3);
    assertThat(attempt1.getUsedAttempts()).isZero();

    // Verify second UserExamAttempt
    UserExamAttempt attempt2 = capturedAttempts.get(1); 
    assertThat(attempt2.getExamId()).isEqualTo("EXAM002");
    assertThat(attempt2.getUserId()).isEqualTo("USER002");
    assertThat(attempt2.getTotalAttempts()).isEqualTo(5);
    assertThat(attempt2.getUsedAttempts()).isZero();
}
}