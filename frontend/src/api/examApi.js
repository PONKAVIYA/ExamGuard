import { getToken } from "../utils/tokenStorage";

export async function getExams() {
  const token = getToken();

  const response = await fetch("/api/exams", {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error("Failed to load exams.");
  }

  return response.json();
}

export async function startExam(examId) {
  const token = getToken();

  const response = await fetch(`/api/exams/${examId}/attempts`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    let message = "Failed to start exam.";

    try {
      const errorData = await response.json();

      if (errorData?.detail) {
        message = errorData.detail;
      }
    } catch {
      // Keep the default message.
    }

    throw new Error(message);
  }

  return response.json();
}

export async function saveAnswer(
  attemptId,
  questionId,
  selectedOptionIndex
) {
  const token = getToken();

  const response = await fetch(
    `/api/attempts/${attemptId}/answers`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        questionId,
        selectedOptionIndex,
      }),
    }
  );

  if (!response.ok) {
    let message = "Failed to save answer.";

    try {
      const errorData = await response.json();

      if (errorData?.detail) {
        message = errorData.detail;
      }
    } catch {
      // Keep the default message.
    }

    throw new Error(message);
  }

  return response.json();
}

export async function submitExam(attemptId) {
  const token = getToken();

  const response = await fetch(
    `/api/attempts/${attemptId}/submit`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!response.ok) {
    let message = "Failed to submit exam.";

    try {
      const errorData = await response.json();

      if (errorData?.detail) {
        message = errorData.detail;
      }
    } catch {
      // Keep the default message.
    }

    throw new Error(message);
  }

  return response.json();
}