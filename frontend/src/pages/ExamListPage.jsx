import { useEffect, useState } from "react";
import { getExams } from "../api/examApi";
import { clearToken } from "../utils/tokenStorage";
import "./ExamListPage.css";

function ExamListPage({ onLogout, onStartExam }) {
  const [exams, setExams] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [startingExamId, setStartingExamId] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadExams() {
      try {
        setError("");

        const data = await getExams();

        setExams(data);
      } catch (err) {
        setError(
          err.message ||
            "Something went wrong while loading the exam list."
        );
      } finally {
        setIsLoading(false);
      }
    }

    loadExams();
  }, []);

  function handleLogout() {
    clearToken();
    onLogout();
  }

  async function handleStartExam(examId) {
    setError("");
    setStartingExamId(examId);

    try {
      await onStartExam(examId);
    } catch (err) {
      setError(
        err.message ||
          "Unable to start the exam. Please try again."
      );
    } finally {
      setStartingExamId(null);
    }
  }

  function formatDuration(seconds) {
    if (!seconds || seconds <= 0) {
      return "Duration not available";
    }

    const minutes = Math.floor(seconds / 60);

    if (minutes < 60) {
      return `${minutes} minute${minutes === 1 ? "" : "s"}`;
    }

    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    if (remainingMinutes === 0) {
      return `${hours} hour${hours === 1 ? "" : "s"}`;
    }

    return `${hours}h ${remainingMinutes}m`;
  }

  return (
    <div className="exam-list-page">
      <header className="exam-list-header">
        <div>
          <h1>ExamGuard</h1>
          <span>Student Portal</span>
        </div>

        <button
          type="button"
          className="logout-button"
          onClick={handleLogout}
        >
          Logout
        </button>
      </header>

      <main className="exam-list-main">
        <div className="exam-list-heading">
          <p className="exam-list-welcome">WELCOME BACK</p>

          <h2>Available Exams</h2>

          <p className="exam-list-subtitle">
            Choose an exam to begin your assessment.
          </p>
        </div>

        {isLoading && (
          <div className="exam-list-message">
            <p>Loading exams...</p>
          </div>
        )}

        {error && (
          <div className="exam-list-error">
            {error}
          </div>
        )}

        {!isLoading && !error && exams.length === 0 && (
          <div className="exam-list-message">
            <p>No exams are available right now.</p>
          </div>
        )}

        {!isLoading && exams.length > 0 && (
          <ul className="exam-list">
            {exams.map((exam) => {
              const isStarting = startingExamId === exam.id;

              return (
                <li
                  key={exam.id}
                  className="exam-card"
                >
                  <div className="exam-card-top">
                    <div className="exam-card-icon">
                      📘
                    </div>

                    <span className="exam-status">
                      Available
                    </span>
                  </div>

                  <div className="exam-card-content">
                    <h3>
                      {exam.title ||
                        exam.name ||
                        `Exam #${exam.id}`}
                    </h3>

                    <p className="exam-id">
                      Exam ID: {exam.id}
                    </p>

                    <div className="exam-divider" />

                    <div className="exam-details">
                      <span>📝 Online Assessment</span>

                      <span>
                        ⏱️{" "}
                        {formatDuration(
                          exam.durationSeconds
                        )}
                      </span>
                    </div>
                  </div>

                  <button
                    type="button"
                    className="start-exam-button"
                    onClick={() =>
                      handleStartExam(exam.id)
                    }
                    disabled={isStarting}
                  >
                    {isStarting
                      ? "Starting..."
                      : "Start Exam →"}
                  </button>
                </li>
              );
            })}
          </ul>
        )}
      </main>
    </div>
  );
}

export default ExamListPage;