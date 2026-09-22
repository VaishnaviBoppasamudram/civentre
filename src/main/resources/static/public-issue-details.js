function getCsrfToken() {

    const cookie = document.cookie
        .split("; ")
        .find(row => row.startsWith("XSRF-TOKEN="));

    return cookie
        ? decodeURIComponent(cookie.split("=")[1])
        : null;
}

const issueId =
    window.location.pathname.split("/").pop();


document.addEventListener("DOMContentLoaded", () => {
    loadIssueDetails();
});


async function loadIssueDetails() {

    try {

        const issueResponse =
            await fetch(`/api/issues/${issueId}`);

        if (!issueResponse.ok) {
            throw new Error("Issue not found");
        }

        const issue =
            await issueResponse.json();


        const historyResponse =
            await fetch(`/api/issues/${issueId}/history`);

        let history = [];

        if (historyResponse.ok) {
            history = await historyResponse.json();
        }


        displayIssue(issue, history);

    } catch (error) {

        console.error(
            "Error loading issue details:",
            error
        );

        document.getElementById(
            "issueContainer"
        ).innerHTML = `
            <div class="error-message">
                Unable to load issue details.
            </div>
        `;
    }
}


function displayIssue(issue, history) {

    const status =
        issue.status || "REPORTED";

    const priority =
        issue.priority || "MEDIUM";


    const reportedImage =
        issue.imagePath
            ? `
                <img
                    src="/uploads/${issue.imagePath}"
                    class="issue-photo"
                    alt="Reported issue image"
                >
              `
            : `
                <p class="no-image">
                    No reported image available.
                </p>
              `;


    const resolutionImage =
        issue.resolutionImagePath
            ? `
                <img
                    src="/uploads/${issue.resolutionImagePath}"
                    class="resolution-photo"
                    alt="Resolution image"
                >
              `
            : `
                <p class="no-image">
                    No resolution photo available yet.
                </p>
              `;


    const historyHtml =
        history.length > 0
            ? history.map(update => `
                <div class="timeline-item">

                    <div class="timeline-status">
                        ${formatStatus(update.status)}
                    </div>

                    <div class="timeline-remarks">
                        ${escapeHtml(
                update.remarks ||
                "Status updated"
            )}
                    </div>

                    <div class="timeline-date">
                        ${formatDate(update.updatedAt)}
                    </div>

                </div>
            `).join("")
            : `
                <p class="no-image">
                    No status history available.
                </p>
              `;


    const mapHtml =
        issue.latitude !== null &&
        issue.longitude !== null
            ? `
                <div id="issueMap"></div>
              `
            : `
                <p class="no-image">
                    Location coordinates are not available.
                </p>
              `;


    document.getElementById(
        "issueContainer"
    ).innerHTML = `

        <section class="issue-header">

            <h1>
                ${escapeHtml(issue.title)}
            </h1>

            <div class="badges">

                <span class="badge status-${status.toLowerCase()}">
                    ${formatStatus(status)}
                </span>

                <span class="badge priority-${priority.toLowerCase()}">
                    ${priority}
                </span>

            </div>

        </section>


        <div class="details-grid">


            <div class="card">

                <h2>
                    Issue Description
                </h2>

                <p class="description">
                    ${escapeHtml(issue.description)}
                </p>

            </div>


            <div class="card">

                <h2>
                    Issue Information
                </h2>

                <div class="info-list">

                    <div class="info-item">
                        <strong>Category:</strong>
                        ${escapeHtml(issue.category)}
                    </div>

                    <div class="info-item">
                        <strong>Location:</strong>
                        ${escapeHtml(issue.location)}
                    </div>

                    <div class="info-item">
                        <strong>Reported:</strong>
                        ${formatDate(issue.createdAt)}
                    </div>

                    <div class="info-item">
                        <strong>Priority:</strong>
                        ${priority}
                    </div>

                </div>

            </div>


            <div class="card">

                <h2>
                    Reported Image
                </h2>

                ${reportedImage}

            </div>


            <div class="card">

                <h2>
                    Resolution Photo
                </h2>

                ${resolutionImage}

            </div>


            <div class="card">

                <h2>
                    Location
                </h2>

                ${mapHtml}

            </div>


            <div class="card">

                <h2>
                    Community Support
                </h2>

                <div class="support-box">

                    <span class="support-count">
                        👍 ${issue.supportCount || 0}
                        supporters
                    </span>

                    ${
        status !== "CLOSED"
            ? `
                                <button
                                    class="support-button"
                                    onclick="supportIssue(${issue.id})">

                                    👍 Support Issue

                                </button>
                              `
            : ""
    }
                </div>
           </div>
            <div class="card">

                <h2>
                    Status History
                </h2>

                <div class="timeline">

                    ${historyHtml}

                </div>

            </div>
  </div>
    `;

    if (
        issue.latitude !== null &&
        issue.longitude !== null
    ) {

        initializeMap(
            issue.latitude,
            issue.longitude
        );

    }
}
function initializeMap(latitude, longitude) {

    const map =
        L.map("issueMap")
            .setView(
                [latitude, longitude],
                15
            );

    L.tileLayer(
        "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        {
            attribution:
                '&copy; OpenStreetMap contributors'
        }
    ).addTo(map);
    L.marker(
        [latitude, longitude]
    )
        .addTo(map)
        .bindPopup("Reported Issue")
        .openPopup();
}
async function supportIssue(issueId) {
    const confirmed =
        confirm(
            "Do you want to support this public issue?"
        );

    if (!confirmed) {
        return;
    }

    try {
        const response =
            await fetch(
                `/api/issues/${issueId}/support`,
                {
                    method: "PUT",
                    headers: {
                        "X-XSRF-TOKEN": getCsrfToken()
                    }
                }
            );
        const data =
            await response.json();


        if (!response.ok) {

            throw new Error(
                data.message ||
                "Failed to support issue"
            );

        }


        alert(
            data.supported
                ? "You supported this issue successfully!"
                : "You have already supported this issue."
        );


        loadIssueDetails();

    } catch (error) {

        console.error(
            "Support issue error:",
            error
        );

        alert(
            "Failed to support issue: " +
            error.message
        );
    }
}
function formatStatus(status) {

    return status
        .replace("_", " ")
        .toLowerCase()
        .replace(/\b\w/g, letter =>
            letter.toUpperCase()
        );
}
function formatDate(date) {

    if (!date) {
        return "Not available";
    }

    return new Date(date)
        .toLocaleString();
}
function escapeHtml(value) {

    if (!value) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}