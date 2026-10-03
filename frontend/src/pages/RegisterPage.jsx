import { useState } from "react";
import { registerUser } from "../api/registerApi";
import "./RegisterPage.css";

function ShieldLogo() {
  return (
    <div className="shield-logo">
      <svg viewBox="0 0 64 72" aria-hidden="true">
        <path
          d="M32 3L57 12V31C57 48 47 61 32 69C17 61 7 48 7 31V12L32 3Z"
          fill="#2456c7"
        />
        <path
          d="M32 12L48 18V31C48 42 42 51 32 57C22 51 16 42 16 31V18L32 12Z"
          fill="#17449f"
        />
        <path
          d="M21 28L32 23L43 28L32 33L21 28Z"
          fill="white"
        />
        <path
          d="M24 31V38C28 41 36 41 40 38V31L32 35L24 31Z"
          fill="white"
        />
        <path
          d="M43 28V38"
          stroke="white"
          strokeWidth="2"
          strokeLinecap="round"
        />
      </svg>
    </div>
  );
}

function UserIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="12" cy="8" r="3.5" />
      <path d="M5 20C5 16.5 8 14 12 14C16 14 19 16.5 19 20" />
    </svg>
  );
}

function EmailIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <rect x="3" y="5" width="18" height="14" rx="2" />
      <path d="M4 7L12 13L20 7" />
    </svg>
  );
}

function LockIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <rect x="5" y="10" width="14" height="10" rx="2" />
      <path d="M8 10V7C8 4.8 9.8 3 12 3C14.2 3 16 4.8 16 7V10" />
    </svg>
  );
}

function ArrowIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="M5 12H19" />
      <path d="M13 6L19 12L13 18" />
    </svg>
  );
}

function RegisterPage({ onBackToLogin }) {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();

    setError("");
    setSuccess("");

    if (!username || !email || !password || !confirmPassword) {
      setError("Please fill in all fields.");
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    if (password.length < 4) {
      setError("Password must be at least 4 characters long.");
      return;
    }

    setIsLoading(true);

    try {
      await registerUser({
        username,
        email,
        password,
      });

      setSuccess(
        "Account created successfully. You can now sign in."
      );

      setUsername("");
      setEmail("");
      setPassword("");
      setConfirmPassword("");
    } catch (err) {
      setError(
        err.message || "Unable to create account. Please try again."
      );
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="register-page">
      <div className="register-background-shape register-background-shape-top" />
      <div className="register-background-shape register-background-shape-bottom" />

      <main className="register-container">

        {/* Branding */}
        <header className="register-brand-header">
          <ShieldLogo />

          <div className="register-brand-text">
            <h1>
              Exam<span>Guard</span>
            </h1>
            <p>Secure Examination Portal</p>
          </div>
        </header>

        {/* Registration Card */}
        <section className="register-card">

          <div className="register-content">

            <div className="register-welcome">
              <h2>Create Account</h2>
              <p>
                Create your student account to start taking exams
              </p>
            </div>

            <form
              onSubmit={handleSubmit}
              className="register-form"
            >

              {/* Username */}
              <div className="register-input-group">
                <label htmlFor="register-username">
                  Username
                </label>

                <div className="register-input-wrapper">
                  <UserIcon />

                  <input
                    id="register-username"
                    type="text"
                    value={username}
                    onChange={(e) =>
                      setUsername(e.target.value)
                    }
                    placeholder="Enter your username"
                    autoComplete="username"
                  />
                </div>
              </div>

              {/* Email */}
              <div className="register-input-group">
                <label htmlFor="register-email">
                  Email Address
                </label>

                <div className="register-input-wrapper">
                  <EmailIcon />

                  <input
                    id="register-email"
                    type="email"
                    value={email}
                    onChange={(e) =>
                      setEmail(e.target.value)
                    }
                    placeholder="Enter your email address"
                    autoComplete="email"
                  />
                </div>
              </div>

              {/* Password */}
              <div className="register-input-group">
                <label htmlFor="register-password">
                  Password
                </label>

                <div className="register-input-wrapper">
                  <LockIcon />

                  <input
                    id="register-password"
                    type="password"
                    value={password}
                    onChange={(e) =>
                      setPassword(e.target.value)
                    }
                    placeholder="Create a password"
                    autoComplete="new-password"
                  />
                </div>
              </div>

              {/* Confirm Password */}
              <div className="register-input-group">
                <label htmlFor="register-confirm-password">
                  Confirm Password
                </label>

                <div className="register-input-wrapper">
                  <LockIcon />

                  <input
                    id="register-confirm-password"
                    type="password"
                    value={confirmPassword}
                    onChange={(e) =>
                      setConfirmPassword(e.target.value)
                    }
                    placeholder="Confirm your password"
                    autoComplete="new-password"
                  />
                </div>
              </div>

              {/* Error */}
              {error && (
                <p className="register-error">
                  {error}
                </p>
              )}

              {/* Success */}
              {success && (
                <p className="register-success">
                  {success}
                </p>
              )}

              {/* Create Account */}
              <button
                type="submit"
                className="register-submit-button"
                disabled={isLoading}
              >
                <span>
                  {isLoading
                    ? "Creating Account..."
                    : "Create Account"}
                </span>

                {!isLoading && <ArrowIcon />}
              </button>
            </form>

            {/* Back to Login */}
            <div className="register-login-section">
              <p>
                Already have an account?{" "}
                <button
                  type="button"
                  className="register-login-link"
                  onClick={onBackToLogin}
                >
                  Login
                </button>
              </p>
            </div>

            <div className="register-footer">
              <span />
              <p>
                Secure&nbsp;&nbsp;•&nbsp;&nbsp;Simple&nbsp;&nbsp;•&nbsp;&nbsp;Reliable
              </p>
              <span />
            </div>

          </div>
        </section>
      </main>
    </div>
  );
}

export default RegisterPage;