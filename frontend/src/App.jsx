import { useState } from "react";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ExamListPage from "./pages/ExamListPage";
import ExamTakingPage from "./pages/ExamTakingPage";
import ResultPage from "./pages/ResultPage";
import { startExam } from "./api/examApi";
import { getToken, clearToken } from "./utils/tokenStorage";
import "./App.css";

function App() {
  const [token, setToken] = useState(getToken());
  const [showRegister, setShowRegister] = useState(false);

  // Current student screen
  const [currentPage, setCurrentPage] = useState("examList");

  // Current exam attempt
  const [attempt, setAttempt] = useState(null);

  // Submission result
  const [result, setResult] = useState(null);

  // General error
  const [error, setError] = useState("");

  function handleLoginSuccess(newToken) {
    setToken(newToken);
    setShowRegister(false);
    setCurrentPage("examList");
    setAttempt(null);
    setResult(null);
    setError("");
  }

  function handleLogout() {
    clearToken();

    setToken(null);
    setShowRegister(false);
    setCurrentPage("examList");
    setAttempt(null);
    setResult(null);
    setError("");
  }

  function handleCreateAccount() {
    setShowRegister(true);
  }

  function handleBackToLogin() {
    setShowRegister(false);
  }

  // ============================================================
  // START EXAM
  // ============================================================

  async function handleStartExam(examId) {
    setError("");

    try {
      const startedAttempt = await startExam(examId);

      setAttempt(startedAttempt);
      setResult(null);
      setCurrentPage("examList");

      // Small delay so React state updates cleanly
      setCurrentPage("exam");
    } catch (err) {
      setError(
        err.message || "Unable to start the exam. Please try again."
      );
    }
  }

  // ============================================================
  // EXAM SUBMITTED
  // ============================================================

  function handleExamSubmitted(submitResult) {
    setResult(submitResult);
    setCurrentPage("result");
  }

  // ============================================================
  // BACK TO EXAM LIST
  // ============================================================

  function handleBackToExams() {
    setAttempt(null);
    setResult(null);
    setError("");
    setCurrentPage("examList");
  }

  // ============================================================
  // NOT LOGGED IN
  // ============================================================

  if (!token) {
    if (showRegister) {
      return (
        <RegisterPage
          onBackToLogin={handleBackToLogin}
        />
      );
    }

    return (
      <LoginPage
        onLoginSuccess={handleLoginSuccess}
        onCreateAccount={handleCreateAccount}
      />
    );
  }

  // ============================================================
  // EXAM TAKING PAGE
  // ============================================================

  if (currentPage === "exam" && attempt) {
    return (
      <ExamTakingPage
        attempt={attempt}
        onSubmitted={handleExamSubmitted}
      />
    );
  }

  // ============================================================
  // RESULT PAGE
  // ============================================================

  if (currentPage === "result" && result) {
    return (
      <ResultPage
        result={result}
        onBackToExams={handleBackToExams}
      />
    );
  }

  // ============================================================
  // EXAM LIST
  // ============================================================

  return (
    <div>
      {error && (
        <div
          style={{
            background: "#fee2e2",
            color: "#b91c1c",
            padding: "1rem",
            textAlign: "center",
            fontWeight: "600",
          }}
        >
          {error}
        </div>
      )}

      <ExamListPage
        onLogout={handleLogout}
        onStartExam={handleStartExam}
      />
    </div>
  );
}

export default App;