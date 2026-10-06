const contextPath = window.APP_CONTEXT_PATH || "";

let fromInput;
let toInput;
let journeyDateInput;
let trainQueryInput;
let reverseButton;
let searchButton;
let stationOptions;
let sortBy;
let stationNames = [];
let currentTrains = [];

window.addEventListener("DOMContentLoaded", function () {

    fromInput = document.getElementById("from");
    toInput = document.getElementById("to");
    journeyDateInput = document.getElementById("journeyDate");
    trainQueryInput = document.getElementById("trainQuery");
    reverseButton = document.getElementById("reverseButton");
    searchButton = document.getElementById("searchButton");
    stationOptions = document.getElementById("stationOptions");
    sortBy = document.getElementById("sortBy");

    setInitialDate();
    loadStations();
    renderDateStrip();

    searchButton.addEventListener("click", searchTrains);

    reverseButton.addEventListener("click", function () {
        const oldFrom = fromInput.value;
        fromInput.value = toInput.value;
        toInput.value = oldFrom;
    });

    journeyDateInput.addEventListener("change", function () {
        renderDateStrip();

        if (fromInput.value.trim() && toInput.value.trim()) {
            searchTrains();
        }
    });

    sortBy.addEventListener("change", function () {
        displayTrains(sortTrains(currentTrains));
    });
});

function setInitialDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    journeyDateInput.min = `${year}-${month}-${day}`;
    journeyDateInput.value = `${year}-${month}-${day}`;
}

function loadStations() {

    fetch(contextPath + "/stations")
        .then(function (response) {
            if (!response.ok) {
                throw new Error("Could not load stations.");
            }
            return response.json();
        })
        .then(function (stations) {

            stationNames = stations;
            stationOptions.innerHTML = "";

            stations.forEach(function (station) {
                const option = document.createElement("option");
                option.value = station;
                stationOptions.appendChild(option);
            });
        })
        .catch(function (error) {
            console.error(error);
        });
}

function searchTrains() {

    const from = fromInput.value.trim();
    const to = toInput.value.trim();
    const date = journeyDateInput.value;
    const trainQuery = trainQueryInput.value.trim();

    const message = document.getElementById("searchMessage");
    const results = document.getElementById("trainResults");

    message.innerHTML = "";
    results.innerHTML = "";
    currentTrains = [];
    document.getElementById("resultCount").textContent = "";

    if (from === "" || to === "") {
        message.innerHTML =
            '<div class="error-message">Please enter both stations.</div>';
        return;
    }

    if (!date) {
        message.innerHTML =
            '<div class="error-message">Please choose a journey date.</div>';
        return;
    }

    if (from.toLowerCase() === to.toLowerCase()) {
        message.innerHTML =
            '<div class="error-message">From and To stations cannot be the same.</div>';
        return;
    }

    const validFrom = stationNames.some(function (station) {
        return station.toLowerCase() === from.toLowerCase();
    });

    const validTo = stationNames.some(function (station) {
        return station.toLowerCase() === to.toLowerCase();
    });

    if (!validFrom || !validTo) {
        message.innerHTML =
            '<div class="error-message">Please select valid stations from the suggestions.</div>';
        return;
    }

    message.innerHTML = "Searching for trains...";

    const url = contextPath
        + "/searchTrains?from="
        + encodeURIComponent(from)
        + "&to="
        + encodeURIComponent(to)
        + "&date="
        + encodeURIComponent(date)
        + "&train="
        + encodeURIComponent(trainQuery);

    fetch(url)
        .then(function (response) {
            return response.json().then(function (data) {
                return {
                    ok: response.ok,
                    data: data
                };
            });
        })
        .then(function (result) {

            if (!result.ok) {
                throw new Error(
                    result.data.error || "Train search failed."
                );
            }

            message.innerHTML = "";
            currentTrains = result.data || [];

            document.getElementById("routeSummary").textContent =
                from + " → " + to;

            document.getElementById("resultCount").textContent =
                currentTrains.length +
                (currentTrains.length === 1 ? " train" : " trains") +
                " on " +
                formatDateText(date);

            renderDateStrip();
            displayTrains(sortTrains(currentTrains));
        })
        .catch(function (error) {
            console.error(error);
            message.innerHTML =
                '<div class="error-message">' +
                escapeHtml(error.message) +
                '</div>';
        });
}

function sortTrains(trains) {

    const sorted = trains.slice();
    const sortValue = sortBy.value;

    sorted.sort(function (a, b) {

        if (sortValue === "fare") {
            return a.fare - b.fare;
        }

        if (sortValue === "duration") {
            return a.durationMinutes - b.durationMinutes;
        }

        if (sortValue === "arrival") {
            return a.arrivalTime.localeCompare(b.arrivalTime);
        }

        if (sortValue === "availability") {
            return b.availableSeats - a.availableSeats;
        }

        return a.departureTime.localeCompare(b.departureTime);
    });

    return sorted;
}

function displayTrains(trains) {

    const results = document.getElementById("trainResults");
    results.innerHTML = "";

    if (!trains || trains.length === 0) {
        results.innerHTML =
            '<div class="empty-state">' +
            '<h3>No trains found</h3>' +
            '<p>No scheduled train is available for this route and date.</p>' +
            '</div>';
        return;
    }

    trains.forEach(function (train) {

        const card = document.createElement("article");
        card.className = "train-card where-my-train-card";

        const seatsText = train.availableSeats === 0
            ? "Fully booked"
            : train.availableSeats + " seats available";

        card.innerHTML = `
            <div class="train-card-header">
                <div>
                    <a class="train-number" href="#"
                       onclick="return false;">
                        ${escapeHtml(train.trainNumber)}
                    </a>
                    <h3>${escapeHtml(train.trainName)}</h3>
                </div>
                <div class="journey-date-pill">
                    ${escapeHtml(train.journeyDateText)}
                </div>
            </div>

            <div class="train-timing-row">
                <div class="timing-block timing-left">
                    <strong>${escapeHtml(train.departureTime)}</strong>
                    <span>${escapeHtml(train.source)}</span>
                    <small>Starting station</small>
                </div>

                <div class="duration-block">
                    <span class="duration-line">— ${escapeHtml(train.durationText)} —</span>
                    <span class="train-line-dot"></span>
                </div>

                <div class="timing-block timing-right">
                    <strong>${escapeHtml(train.arrivalTime)}</strong>
                    <span>${escapeHtml(train.destination)}</span>
                    <small>Last station</small>
                </div>
            </div>

            <div class="train-card-bottom">
                <div class="fare-box">
                    <span>Fare / seat</span>
                    <strong>₹${Number(train.fare).toFixed(2)}</strong>
                </div>

                <div class="availability-box">
                    <span>Availability</span>
                    <strong>${escapeHtml(seatsText)}</strong>
                </div>

                <button type="button"
                        class="button primary-button"
                        onclick="selectTrain(${train.scheduleId})"
                        ${train.availableSeats === 0 ? "disabled" : ""}>
                    ${train.availableSeats === 0 ? "Fully Booked" : "Select Train"}
                </button>
            </div>
        `;

        results.appendChild(card);
    });
}

function renderDateStrip() {

    const strip = document.getElementById("dateStrip");
    strip.innerHTML = "";

    if (!journeyDateInput.value) {
        return;
    }

    const selected = new Date(journeyDateInput.value + "T12:00:00");

    for (let offset = -3; offset <= 3; offset++) {

        const day = new Date(selected);
        day.setDate(selected.getDate() + offset);

        const dateValue = formatInputDate(day);
        const button = document.createElement("button");
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        button.type = "button";
        button.disabled = day < today;
        button.className = "date-item";

        if (dateValue === journeyDateInput.value) {
            button.classList.add("selected-date");
        }

        button.innerHTML = `
            <span class="date-day-name">${day.toLocaleDateString("en-IN", { weekday: "short" })}</span>
            <strong>${String(day.getDate()).padStart(2, "0")}</strong>
            <span class="date-month">${day.toLocaleDateString("en-IN", { month: "short" })}</span>
        `;

        button.addEventListener("click", function () {
            journeyDateInput.value = dateValue;
            renderDateStrip();

            if (fromInput.value.trim() && toInput.value.trim()) {
                searchTrains();
            }
        });

        strip.appendChild(button);
    }
}

function formatInputDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function formatDateText(dateText) {
    const date = new Date(dateText + "T12:00:00");

    return date.toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    });
}

function selectTrain(scheduleId) {
    window.location.href =
        contextPath +
        "/user/seat-selection.jsp?scheduleId=" +
        encodeURIComponent(scheduleId);
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
