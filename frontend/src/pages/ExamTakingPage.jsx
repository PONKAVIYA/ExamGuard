
import { useEffect, useState } from "react";
import { saveAnswer, submitExam } from "../api/examApi";
import "./ExamTakingPage.css";

function ExamTakingPage({ attempt, onSubmitted }) {
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState({});
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const questions = attempt.questions || [];

  // Support different possible backend property names.
  const endTimeValue =
    attempt.endsAt ??
    attempt.endAt ??
    attempt.endedAt ??
    attempt.endTime;

  const [timeLeft, setTimeLeft] = useState(() => {
    const endTime = new Date(endTimeValue).getTime();

    if (Number.isNaN(endTime)) {
      return 0;
    }

    return Math.max(
      0,
      Math.floor((endTime - Date.now()) / 1000)
    );
  });

  useEffect(() => {
    const endTime = new Date(endTimeValue).getTime();

    if (Number.isNaN(endTime)) {
      setError("Invalid exam end time received from server.");
      return;
    }

    function updateTimer() {
      const remaining = Math.max(
        0,
        Math.floor((endTime - Date.now()) / 1000)
      );

      setTimeLeft(remaining);

      if (remaining === 0) {
        handleSubmit();
      }
    }

    updateTimer();

    const timer = setInterval(updateTimer, 1000);

    return () => clearInterval(timer);
  }, [endTimeValue]);

  async function handleAnswerChange(questionId, optionIndex) {
    setAnswers((previous) => ({
      ...previous,
      [questionId]: optionIndex,
    }));

    setSaving(true);
    setError("");

    try {
      await saveAnswer(
        attempt.attemptId,
        questionId,
        optionIndex
      );
    } catch (err) {
      setError(err.message || "Failed to save answer.");
    } finally {
      setSaving(false);
    }
  }

  async function handleSubmit() {
    try {
      setError("");

      const result = await submitExam(attempt.attemptId);

      onSubmitted(result);
    } catch (err) {
      setError(err.message || "Failed to submit exam.");
    }
  }

  function formatTime(seconds) {
    if (!Number.isFinite(seconds) || seconds < 0) {
      return "00:00";
    }

    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    return `${String(minutes).padStart(2, "0")}:${String(
      remainingSeconds
    ).padStart(2, "0")}`;
  }

  if (questions.length === 0) {
    return (
      <div className="exam-taking-page">
        <div className="exam-taking-empty">
          <h2>No questions available</h2>
          <p>This exam currently has no questions.</p>
        </div>
      </div>
    );
  }

  const question = questions[currentQuestion];

  const questionId =
    question.questionId ?? question.id;

  const selectedAnswer = answers[questionId];

  return (
    <div className="exam-taking-page">
      <header className="exam-taking-header">
        <div>
          <h1>ExamGuard</h1>
          <span>Online Examination</span>
        </div>

        <div className="timer">
          <span>Time Remaining</span>
          <strong>{formatTime(timeLeft)}</strong>
        </div>
      </header>

      <main className="exam-taking-content">
        <div className="question-progress">
          Question {currentQuestion + 1} of {questions.length}
        </div>

        <div className="question-card">
          <h2>{question.text}</h2>

          <div className="options">
            {question.options?.map((option) => (
              <label
                key={option.id}
                className={`option ${
                  selectedAnswer === option.orderIndex
                    ? "selected"
                    : ""
                }`}
              >
                <input
                  type="radio"
                  name={`question-${questionId}`}
                  checked={
                    selectedAnswer === option.orderIndex
                  }
                  onChange={() =>
                    handleAnswerChange(
                      questionId,
                      option.orderIndex
                    )
                  }
                />

                <span>{option.text}</span>
              </label>
            ))}
          </div>
        </div>

        {saving && (
          <p className="saving-message">
            Saving answer...
          </p>
        )}

        {error && (
          <div className="exam-taking-error">
            {error}
          </div>
        )}

        <div className="question-navigation">
          <button
            type="button"
            onClick={() =>
              setCurrentQuestion(
                (current) => current - 1
              )
            }
            disabled={currentQuestion === 0}
          >
            ← Previous
          </button>

          {currentQuestion < questions.length - 1 ? (
            <button
              type="button"
              onClick={() =>
                setCurrentQuestion(
                  (current) => current + 1
                )
              }
            >
              Next →
            </button>
          ) : (
            <button
              type="button"
              className="submit-button"
              onClick={handleSubmit}
            >
              Submit Exam
            </button>
          )}
        </div>
      </main>
    </div>
  );
}

export default ExamTakingPage;