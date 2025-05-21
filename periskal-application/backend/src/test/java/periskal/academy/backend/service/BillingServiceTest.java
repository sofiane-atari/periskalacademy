package periskal.academy.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import periskal.academy.backend.jpa.Billing;
import periskal.academy.backend.jpa.BillingRepository;
import periskal.academy.backend.jpa.UserExamAttemptRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {

    @Mock
    private BillingRepository billingRepository;

    @InjectMocks
    private BillingService billingService;

    @Mock
    private UserExamAttemptRepository userExamAttemptRepository;

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

        when(billingRepository.save(nullBilling))
                .thenThrow(new IllegalArgumentException("Entity to save must not be null"));

        // Act: Use AssertJ's assertThatThrownBy for asserting exceptions
        assertThatThrownBy(() -> billingService.saveOrder(nullBilling))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity to save must not be null");

        // Assert that the save method on the repository was called once with null
        verify(billingRepository, times(1)).save(nullBilling);
    }
}