package periskal.academy.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import periskal.academy.backend.jpa.UserExamAttempt;
import periskal.academy.backend.jpa.UserExamAttemptRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserExamAttemptServiceTest {

        @Mock
        private UserExamAttemptRepository userExamAttemptRepository;

        @InjectMocks
        private UserExamAttemptService userExamAttemptService;

        private String userId;
        private String examId;

        @BeforeEach
        void setUp() {
                userId = "testUser";
                examId = "testExam";
        }

        @Test
        void givenNoActiveAttempts_whenConsumeAttemptIsCalled_thenReturnFalse() {
                when(userExamAttemptRepository.findAllByUserIdAndExamId(userId, examId))
                                .thenReturn(Collections.emptyList());

                boolean result = userExamAttemptService.consumeAttempt(userId, examId);

                assertThat(result).isFalse();
                verify(userExamAttemptRepository, never()).save(any(UserExamAttempt.class));
        }

        @Test
        @DisplayName("Given an active attempt with uses left, when consumeAttempt is called, then return true and save updated attempt")
        void givenActiveAttemptWithUsesLeft_whenConsumeAttemptIsCalled_thenReturnTrueAndSaveAttempt() {
                UserExamAttempt attempt = new UserExamAttempt();
                attempt.setId(1L);
                attempt.setUserId(userId);
                attempt.setExamId(examId);
                attempt.setTotalAttempts(2);
                attempt.setUsedAttempts(0);

                List<UserExamAttempt> attempts = Collections.singletonList(attempt);

                when(userExamAttemptRepository.findAllByUserIdAndExamId(userId, examId))
                                .thenReturn(attempts);
                when(userExamAttemptRepository.save(any(UserExamAttempt.class)))
                                .thenReturn(attempt);

                boolean result = userExamAttemptService.consumeAttempt(userId, examId);

                assertThat(result).isTrue();
                assertThat(attempt.getUsedAttempts()).isEqualTo(1);
                assertThat(attempt.getRemainingAttempts()).isEqualTo(1);
                verify(userExamAttemptRepository, times(1)).save(attempt);
        }

        @Test
        @DisplayName("Given an active attempt with no uses left, when consumeAttempt is called, then return false")
        void givenActiveAttemptWithNoUsesLeft_whenConsumeAttemptIsCalled_thenReturnFalse() {
                UserExamAttempt attempt = new UserExamAttempt();
                attempt.setId(1L);
                attempt.setUserId(userId);
                attempt.setExamId(examId);
                attempt.setTotalAttempts(2);
                attempt.setUsedAttempts(2);

                List<UserExamAttempt> attempts = Collections.singletonList(attempt);

                when(userExamAttemptRepository.findAllByUserIdAndExamId(userId, examId))
                                .thenReturn(attempts);

                boolean result = userExamAttemptService.consumeAttempt(userId, examId);

                assertThat(result).isFalse();
                assertThat(attempt.getUsedAttempts()).isEqualTo(2);
                verify(userExamAttemptRepository, never()).save(any(UserExamAttempt.class));
        }

        @Test
        @DisplayName("Given multiple attempts including one with uses left, when consumeAttempt is called, then use the first available attempt")
        void givenMultipleAttempts_whenConsumeAttemptIsCalled_thenUseFirstActiveOne() {
                UserExamAttempt inactiveAttempt = new UserExamAttempt();
                inactiveAttempt.setId(1L);
                inactiveAttempt.setUserId(userId);
                inactiveAttempt.setExamId(examId);
                inactiveAttempt.setTotalAttempts(1);
                inactiveAttempt.setUsedAttempts(1);

                UserExamAttempt activeAttempt = new UserExamAttempt();
                activeAttempt.setId(2L);
                activeAttempt.setUserId(userId);
                activeAttempt.setExamId(examId);
                activeAttempt.setTotalAttempts(1);
                activeAttempt.setUsedAttempts(0);

                List<UserExamAttempt> attempts = Arrays.asList(inactiveAttempt, activeAttempt);

                when(userExamAttemptRepository.findAllByUserIdAndExamId(userId, examId))
                                .thenReturn(attempts);
                when(userExamAttemptRepository.save(any(UserExamAttempt.class)))
                                .thenReturn(activeAttempt);

                boolean result = userExamAttemptService.consumeAttempt(userId, examId);

                assertThat(result).isTrue();
                assertThat(activeAttempt.getUsedAttempts()).isEqualTo(1);
                assertThat(activeAttempt.getRemainingAttempts()).isEqualTo(0);
                assertThat(inactiveAttempt.getUsedAttempts()).isEqualTo(1);
                verify(userExamAttemptRepository, times(1)).save(activeAttempt);
        }

        @Test
        @DisplayName("Given an active attempt, when its useAttempt throws IllegalStateException, then return false")
        void givenActiveAttempt_whenUseAttemptThrowsIllegalStateException_thenReturnFalse() {
                UserExamAttempt problematicAttempt = new UserExamAttempt();
                problematicAttempt.setId(1L);
                problematicAttempt.setUserId(userId);
                problematicAttempt.setExamId(examId);
                problematicAttempt.setTotalAttempts(1);
                problematicAttempt.setUsedAttempts(0);

                UserExamAttempt spyAttempt = spy(problematicAttempt);
                when(spyAttempt.hasAttemptsLeft()).thenReturn(true);
                doThrow(new IllegalStateException("No remaining attempts.")).when(spyAttempt).useAttempt();

                List<UserExamAttempt> attempts = Collections.singletonList(spyAttempt);
                when(userExamAttemptRepository.findAllByUserIdAndExamId(userId, examId))
                                .thenReturn(attempts);

                boolean result = userExamAttemptService.consumeAttempt(userId, examId);

                assertThat(result).isFalse();
                verify(userExamAttemptRepository, never()).save(any(UserExamAttempt.class));
        }
}