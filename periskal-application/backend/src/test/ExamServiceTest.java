@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private ExamService examService;

    @Test
    void saveExam_ShouldSaveWithQuestions() {
        Exam exam = new Exam("Spring Test", "Spring Framework");
        exam.setQuestions(List.of(
                new Question("What is DI?"),
                new Question("What is AOP?")));

        when(examRepository.save(any(Exam.class))).thenReturn(exam);

        Exam savedExam = examService.saveExam(exam);

        assertNotNull(savedExam);
        assertEquals(2, savedExam.getQuestions().size());
        verify(questionRepository, times(2)).save(any(Question.class));
    }
}