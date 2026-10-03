
export async function loginUser(username, password) {
  const response = await fetch("/api/authenticate", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username,
      password,
      rememberMe: false,
    }),
  });

  if (!response.ok) {
    let message = "Login failed. Please check your username and password.";

    try {
      const errorData = await response.json();

      if (errorData && (errorData.detail || errorData.title)) {
        message = errorData.detail || errorData.title;
      }
    } catch (e) {
      // Keep the default message if the response isn't JSON.
    }

    throw new Error(message);
  }

  const data = await response.json();
  return data.id_token;
}