function ResultPage({ result, onBackToExams }) {
  return (
    <div
      style={{
        minHeight: "100vh",
        background: "#f3f7fc",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "2rem",
        fontFamily: "Arial, sans-serif",
      }}
    >
      <div
        style={{
          background: "white",
          width: "100%",
          maxWidth: "550px",
          padding: "3rem 2rem",
          borderRadius: "20px",
          textAlign: "center",
          boxShadow: "0 10px 30px rgba(0,0,0,0.12)",
        }}
      >
        <div
          style={{
            fontSize: "3.5rem",
            marginBottom: "1rem",
          }}
        >
          🎉
        </div>

        <h1
          style={{
            color: "#173b7a",
            marginBottom: "0.5rem",
          }}
        >
          Exam Submitted!
        </h1>

        <p
          style={{
            color: "#6b7280",
            fontSize: "1rem",
          }}
        >
          Your exam has been successfully submitted.
        </p>

        <div
          style={{
            margin: "2rem 0",
            padding: "1.5rem",
            background: "#f0f6ff",
            borderRadius: "15px",
          }}
        >
          <p
            style={{
              margin: 0,
              color: "#6b7280",
              fontSize: "1rem",
            }}
          >
            Your Score
          </p>

          <div
            style={{
              marginTop: "0.5rem",
              fontSize: "3rem",
              fontWeight: "700",
              color: "#2563eb",
            }}
          >
            {result?.score ?? 0} / {result?.total ?? 0}
          </div>
        </div>

        <button
          type="button"
          onClick={onBackToExams}
          style={{
            width: "100%",
            padding: "0.9rem",
            border: "none",
            borderRadius: "10px",
            background: "#1976d2",
            color: "white",
            fontSize: "1rem",
            fontWeight: "600",
            cursor: "pointer",
          }}
        >
          Back to Exams
        </button>
      </div>
    </div>
  );
}

export default ResultPage;