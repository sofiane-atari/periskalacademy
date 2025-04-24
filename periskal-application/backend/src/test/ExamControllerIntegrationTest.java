@SpringBootTest
@AutoConfigureMockMvc
class ExamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createExam_ShouldReturnCreatedExam() throws Exception {
        ExamRequest request = new ExamRequest();
        request.setExamTitle("Java Test");
        request.setExamDescription("Basics of Java");
        request.setTrainingId(1L);
        request.setQuestions(List.of(
                new QuestionDto("What is OOP?"),
                new QuestionDto("What is inheritance?")));

        mockMvc.perform(post("/api/exams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.examTitle").value("Java Test"))
                .andExpect(jsonPath("$.questions.length()").value(2));
    }
}