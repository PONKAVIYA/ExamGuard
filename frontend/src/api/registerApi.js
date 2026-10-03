export async function registerUser({
  username,
  email,
  password,
}) {
  const response = await fetch("/api/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      login: username,
      email: email,
      password: password,
      langKey: "en",
    }),
  });

  if (!response.ok) {
    let message = "Registration failed.";

    try {
      const errorData = await response.json();

      if (errorData?.detail) {
        message = errorData.detail;
      } else if (errorData?.message) {
        message = errorData.message;
      } else if (errorData?.title) {
        message = errorData.title;
      }
    } catch {
      // Keep the default message.
    }

    throw new Error(message);
  }
}