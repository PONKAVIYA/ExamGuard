import { useState } from "react";
import { loginUser } from "../api/authApi";
import { saveToken } from "../utils/tokenStorage";
import "./LoginPage.css";

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

function AcademicIllustration() {
  return (
    <div className="academic-illustration" aria-hidden="true">
      <svg viewBox="0 0 360 300">
        {/* Decorative leaves */}
        <path
          d="M65 190C35 165 24 137 38 115C67 132 78 157 65 190Z"
          fill="#d7e5ff"
        />
        <path
          d="M48 157C24 143 18 119 28 101C52 111 61 134 48 157Z"
          fill="#cbdcff"
        />
        <path
          d="M83 128C62 108 62 84 77 72C94 91 96 110 83 128Z"
          fill="#dce8ff"
        />

        {/* Books */}
        <rect
          x="75"
          y="208"
          width="210"
          height="27"
          rx="5"
          fill="#b9cdf2"
          transform="rotate(-2 75 208)"
        />
        <rect
          x="62"
          y="231"
          width="225"
          height="29"
          rx="5"
          fill="#c8d9f7"
          transform="rotate(3 62 231)"
        />
        <rect
          x="75"
          y="256"
          width="215"
          height="25"
          rx="5"
          fill="#d7e4fa"
          transform="rotate(-1 75 256)"
        />

        {/* Graduation cap */}
        <path
          d="M82 145L178 112L274 145L178 178L82 145Z"
          fill="#9eb8e9"
        />
        <path
          d="M109 153V184C144 207 211 207 247 184V153L178 177L109 153Z"
          fill="#b4c9ef"
        />
        <path
          d="M274 145V190"
          stroke="#7e9ed8"
          strokeWidth="5"
          strokeLinecap="round"
        />
        <circle cx="274" cy="194" r="7" fill="#7e9ed8" />
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

function LoginPage({ onLoginSuccess, onCreateAccount }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    if (!username || !password) {
      setError("Please enter both your username and password.");
      return;
    }

    setIsLoading(true);

    try {
      const token = await loginUser(username, password);
      saveToken(token);
      onLoginSuccess(token);
    } catch (err) {
      setError(err.message || "Something went wrong. Please try again.");
    } finally {
      setIsLoading(false);
    }
  }

  function handleCreateAccount() {
    onCreateAccount();
  }

  return (
    <div className="login-page">
      <div className="background-shape background-shape-top" />
      <div className="background-shape background-shape-bottom" />

      <main className="login-container">
        <header className="brand-header">
          <ShieldLogo />

          <div className="brand-text">
            <h1>
              Exam<span>Guard</span>
            </h1>
            <p>Secure Examination Portal</p>
          </div>
        </header>

        <section className="login-card">
          <div className="illustration-area">
            <AcademicIllustration />
          </div>

          <div className="login-content">
            <div className="welcome-section">
              <h2>Welcome Back</h2>
              <p>Sign in to your account to continue</p>
            </div>

            <form onSubmit={handleSubmit} className="login-form">
              <div className="input-group">
                <label htmlFor="username">Username</label>

                <div className="input-wrapper">
                  <UserIcon />

                  <input
                    id="username"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Enter your username"
                    autoComplete="username"
                  />
                </div>
              </div>

              <div className="input-group">
                <label htmlFor="password">Password</label>

                <div className="input-wrapper">
                  <LockIcon />

                  <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    autoComplete="current-password"
                  />
                </div>
              </div>

              {error && <p className="login-error">{error}</p>}

              <button type="submit" disabled={isLoading}>
                <span>{isLoading ? "Signing in..." : "Login"}</span>
                {!isLoading && <ArrowIcon />}
              </button>
            </form>

            <div className="login-footer">
              <p className="register-prompt">
                Don't have an account?{" "}
                <button
                  type="button"
                  className="register-link"
                  onClick={handleCreateAccount}
                >
                  Create Account
                </button>
              </p>

              <div className="footer-divider">
                <span />
                <p>
                  Secure&nbsp;&nbsp;•&nbsp;&nbsp;Simple&nbsp;&nbsp;•&nbsp;&nbsp;Reliable
                </p>
                <span />
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}

export default LoginPage;