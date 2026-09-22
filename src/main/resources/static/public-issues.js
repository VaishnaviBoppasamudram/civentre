let allIssues = [];


document.addEventListener("DOMContentLoaded", () => {

    loadPublicIssues();

    document
        .getElementById("searchInput")
        .addEventListener("input", filterIssues);

    document
        .getElementById("categoryFilter")
        .addEventListener("change", filterIssues);

    document
        .getElementById("statusFilter")
        .addEventListener("change", filterIssues);

});


async function loadPublicIssues() {

    try {

        const response = await fetch("/api/issues/public");

        if (!response.ok) {
            throw new Error("Failed to load public issues");
        }

        allIssues = await response.json();

        displayIssues(allIssues);

    } catch (error) {

        console.error("Error loading public issues:", error);

        document.getElementById("issuesContainer").innerHTML = `
            <p class="empty-message">
                Unable to load public issues.
            </p>
        `;
    }
}


function displayIssues(issues) {

    const container =
        document.getElementById("issuesContainer");

    if (issues.length === 0) {

        container.innerHTML = `
            <p class="empty-message">
                No public issues found.
            </p>
        `;

        return;
    }


    container.innerHTML = issues.map(issue => {

        const status =
            issue.status || "REPORTED";

        const priority =
            issue.priority || "MEDIUM";


        const imageHtml =
            issue.imagePath
                ? `
                    <img
                        src="/uploads/${issue.imagePath}"
                        class="issue-image"
                        alt="Issue image"
                    >
                  `
                : "";


        return `
            <div class="issue-card">

                ${imageHtml}

                <h3>
                    ${escapeHtml(issue.title)}
                </h3>

                <p class="issue-description">
                    ${escapeHtml(issue.description)}
                </p>


                <div class="badges">

                    <span class="badge status-${status.toLowerCase()}">
                        ${formatStatus(status)}
                    </span>

                    <span class="badge priority-${priority.toLowerCase()}">
                        ${priority}
                    </span>

                </div>


                <div class="issue-info">

                    <span>
                        📂 ${escapeHtml(issue.category)}
                    </span>

                    <span>
                        📍 ${escapeHtml(issue.location)}
                    </span>

                </div>


                <div class="support-row">

                    <span class="support-count">
                        👍 ${issue.supportCount || 0} supporters
                    </span>

                    <button
                        class="view-button"
                        onclick="viewIssue(${issue.id})">

                        View Details

                    </button>

                </div>

            </div>
        `;

    }).join("");
}


function filterIssues() {

    const searchText =
        document
            .getElementById("searchInput")
            .value
            .toLowerCase();

    const category =
        document
            .getElementById("categoryFilter")
            .value;

    const status =
        document
            .getElementById("statusFilter")
            .value;


    const filteredIssues =
        allIssues.filter(issue => {

            const matchesSearch =
                (issue.title || "")
                    .toLowerCase()
                    .includes(searchText)
                ||
                (issue.description || "")
                    .toLowerCase()
                    .includes(searchText)
                ||
                (issue.location || "")
                    .toLowerCase()
                    .includes(searchText);


            const matchesCategory =
                !category ||
                issue.category === category;


            const matchesStatus =
                !status ||
                issue.status === status;


            return (
                matchesSearch &&
                matchesCategory &&
                matchesStatus
            );

        });


    displayIssues(filteredIssues);
}


function formatStatus(status) {

    return status
        .replace("_", " ")
        .toLowerCase()
        .replace(/\b\w/g, letter =>
            letter.toUpperCase()
        );
}


function viewIssue(issueId) {

    window.location.href =
        `/issue-details/${issueId}`;
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