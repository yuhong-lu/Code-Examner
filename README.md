src/main/java/com/CodeExamner/
├── CodeExamnerApplication.java          # 启动类
├── config/                             # 配置类
│   ├── RabbitMQConfig.java             # 消息队列配置
│   ├── AsyncConfig.java                # 异步配置
│   ├── SwaggerConfig.java              # API文档配置
│   └── Judge0Config.java               # 评测服务配置
├── controller/                         # 控制器层
│   ├── AuthController.java
│   ├── ExamController.java
│   ├── ProblemController.java
│   ├── SubmissionController.java
│   ├── AdminController.java
│   └── UserController.java
├── dto/                               # 数据传输对象
│   ├── request/                       # 请求DTO
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── ExamCreateRequest.java
│   │   ├── ProblemCreateRequest.java
│   │   ├── SubmissionRequest.java
│   │   └── JudgeTask.java
│   └── response/                      # 响应DTO
│       ├── AuthResponse.java
│       ├── ExamResponse.java
│       ├── ProblemResponse.java
│       ├── SubmissionResponse.java
│       ├── StatisticsResponse.java
│       └── ApiResponse.java
├── entity/                            # 实体类
│   ├── User.java
│   ├── Student.java
│   ├── Problem.java
│   ├── Exam.java
│   ├── ExamProblem.java
│   ├── Submission.java
│   ├── SubmissionDetail.java
│   ├── TestCase.java
│   └── enums/                         # 枚举类
│       ├── UserRole.java
│       ├── ExamStatus.java
│       ├── Difficulty.java
│       └── JudgeStatus.java
├── exception/                         # 异常处理
│   ├── GlobalExceptionHandler.java
│   ├── BusinessException.java
│   ├── AuthenticationException.java
│   └── error/                         # 错误信息
│       ├── ErrorCode.java
│       └── ApiError.java
├── mq/                                # 消息队列
│   ├── producer/
│   │   └── JudgeTaskProducer.java
│   ├── consumer/
│   │   └── JudgeTaskConsumer.java
│   └── message/
│       └── JudgeTaskMessage.java
├── repository/                        # 数据访问层
│   ├── UserRepository.java
│   ├── StudentRepository.java
│   ├── ProblemRepository.java
│   ├── ExamRepository.java
│   ├── ExamProblemRepository.java
│   ├── SubmissionRepository.java
│   ├── SubmissionDetailRepository.java
│   └── TestCaseRepository.java
├── security/                          # 安全配置
│   ├── SecurityConfig.java
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
├── service/                           # 服务层
│   ├── UserService.java
│   ├── ExamService.java
│   ├── ProblemService.java
│   ├── SubmissionService.java
│   ├── StatisticsService.java
│   ├── JudgeService.java
│   └── impl/                          # 服务实现类
│       ├── UserServiceImpl.java
│       ├── ExamServiceImpl.java
│       ├── ProblemServiceImpl.java
│       ├── SubmissionServiceImpl.java
│       └── StatisticsServiceImpl.java
├── util/                             # 工具类
│   ├── DateUtil.java
│   ├── StringUtil.java
│   ├── ValidationUtil.java
│   └── JsonUtil.java
└── judge0/                           # Judge0集成
    ├── client/
    │   ├── Judge0ApiClient.java
    │   ├── Judge0Submission.java
    │   └── Judge0Result.java
    ├── service/
    │   └── Judge0IntegrationService.java
    └── config/
        └── Judge0Properties.java
