package periskal.academy.backend.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import periskal.academy.backend.jpa.faq.Faq;
import periskal.academy.backend.jpa.faq.FaqRepository;
import periskal.academy.backend.security.exceptions.general.*;

class FaqServiceTest {

    @InjectMocks
    private FaqService faqService;

    @Mock
    private FaqRepository faqRepository;

    private Faq sampleFaq;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        sampleFaq = new Faq();
        sampleFaq.setId(1L);
        sampleFaq.setQuestionEn("What is Java?");
        sampleFaq.setAnswerEn("A programming language.");
    }

    @Test
    void getAllFaqs_shouldReturnFaqList() {
        when(faqRepository.findAll()).thenReturn(List.of(sampleFaq));

        List<Faq> faqs = faqService.getAllFaqs();

        assertThat(faqs).hasSize(1);
        assertThat(faqs.get(0).getQuestionEn()).isEqualTo("What is Java?");
    }

    @Test
    void getAllFaqs_whenDbFails_shouldThrowRuntimeException() {
        when(faqRepository.findAll()).thenThrow(new DataAccessException("DB error") {
        });

        assertThatThrownBy(() -> faqService.getAllFaqs())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not fetch FAQs");
    }

    @Test
    void getFaqById_whenExists_shouldReturnFaq() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(sampleFaq));

        Optional<Faq> result = faqService.getFaqById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getFaqById_whenNotFound_shouldThrowResourceNotFound() {
        when(faqRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> faqService.getFaqById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("FAQ not found with id");
    }

    @Test
    void createFaq_whenValid_shouldReturnSavedFaq() {
        when(faqRepository.save(any())).thenReturn(sampleFaq);

        Faq result = faqService.createFaq(sampleFaq);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void createFaq_whenInvalid_shouldThrowValidationException() {
        Faq invalidFaq = new Faq(); // No question or answer

        assertThatThrownBy(() -> faqService.createFaq(invalidFaq))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid FAQ data");
    }

    @Test
    void createFaq_whenDataIntegrityViolation_shouldThrow() {
        when(faqRepository.save(any())).thenThrow(new DataIntegrityViolationException("Violation"));

        assertThatThrownBy(() -> faqService.createFaq(sampleFaq))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void updateFaq_whenExists_shouldUpdateAndReturn() {
        Faq updatedFaq = new Faq();
        updatedFaq.setQuestionEn("Updated?");
        updatedFaq.setAnswerEn("Yes.");

        when(faqRepository.findById(1L)).thenReturn(Optional.of(sampleFaq));
        when(faqRepository.save(any())).thenReturn(sampleFaq);

        Faq result = faqService.updateFaq(1L, updatedFaq);

        assertThat(result).isNotNull();
        verify(faqRepository).save(any(Faq.class));
    }

    @Test
    void updateFaq_whenNotFound_shouldThrowResourceNotFound() {
        when(faqRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> faqService.updateFaq(1L, sampleFaq))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteFaq_whenExists_shouldDelete() {
        when(faqRepository.existsById(1L)).thenReturn(true);
        doNothing().when(faqRepository).deleteById(1L);

        faqService.deleteFaq(1L);

        verify(faqRepository).deleteById(1L);
    }

    @Test
    void deleteFaq_whenNotFound_shouldThrowResourceNotFound() {
        when(faqRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> faqService.deleteFaq(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
