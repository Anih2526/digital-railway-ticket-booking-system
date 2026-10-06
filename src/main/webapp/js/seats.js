const contextPath = window.APP_CONTEXT_PATH || "";

const seatPage = document.getElementById("seatPage");
const scheduleId = seatPage.dataset.scheduleId;

const seatContainer = document.getElementById("seatContainer");
const seatMessage = document.getElementById("seatMessage");
const selectedSeatsText = document.getElementById("selectedSeats");
const totalAmountText = document.getElementById("totalAmount");
const bookButton = document.getElementById("bookButton");

const selectedSeats = new Map();
let pricePerSeat = 0;

window.addEventListener("DOMContentLoaded", loadSeats);

bookButton.addEventListener("click", bookSelectedSeats);

function loadSeats() {

    if (!scheduleId) {
        showMessage("No schedule was selected.");
        return;
    }

    fetch(
        contextPath +
        "/getSeats?scheduleId=" +
        encodeURIComponent(scheduleId)
    )
        .then(function (response) {
            if (!response.ok) {
                throw new Error("Unable to load seats.");
            }
            return response.json();
        })
        .then(function (seats) {
            if (!seats || seats.length === 0) {
                displaySeats(seats);
                return;
            }

            pricePerSeat = Number(seats[0].fare || 0);
            displaySeats(seats);
            updateSummary();
        })
        .catch(function (error) {
            showMessage(error.message);
        });
}

function displaySeats(seats) {

    seatContainer.innerHTML = "";

    if (!seats || seats.length === 0) {
        showMessage("No seats are available for this schedule.");
        return;
    }

    const coaches = {};

    seats.forEach(function (seat) {

        if (!coaches[seat.coachName]) {
            coaches[seat.coachName] = [];
        }

        coaches[seat.coachName].push(seat);
    });

    Object.keys(coaches).forEach(function (coachName) {

        const coachCard = document.createElement("div");
        coachCard.className = "coach-card";

        const title = document.createElement("h3");
        title.className = "coach-title";
        title.textContent = "Coach " + coachName;

        const grid = document.createElement("div");
        grid.className = "seat-grid";

        coaches[coachName].forEach(function (seat) {

            const button = document.createElement("button");

            button.type = "button";
            button.className = "seat-button";
            button.textContent = seat.seatNumber;

            if (seat.status === "BOOKED") {
                button.classList.add("booked");
                button.disabled = true;
            } else {
                button.addEventListener("click", function () {
                    toggleSeat(button, seat);
                });
            }

            grid.appendChild(button);
        });

        coachCard.appendChild(title);
        coachCard.appendChild(grid);
        seatContainer.appendChild(coachCard);
    });
}

function toggleSeat(button, seat) {

    if (selectedSeats.has(seat.scheduleSeatId)) {
        selectedSeats.delete(seat.scheduleSeatId);
        button.classList.remove("selected");
    } else {
        selectedSeats.set(seat.scheduleSeatId, {
            coachName: seat.coachName,
            seatNumber: seat.seatNumber
        });
        button.classList.add("selected");
    }

    updateSummary();
}

function updateSummary() {

    const seats = Array.from(selectedSeats.values());

    if (seats.length === 0) {
        selectedSeatsText.textContent = "None";
        totalAmountText.textContent = "₹0.00";
        bookButton.disabled = true;
        return;
    }

    const names = seats.map(function (seat) {
        return seat.coachName + "-" + seat.seatNumber;
    });

    selectedSeatsText.textContent = names.join(", ");

    const total = seats.length * pricePerSeat;

    totalAmountText.textContent =
        "₹" + total.toFixed(2);

    bookButton.disabled = false;
}

function bookSelectedSeats() {

    if (selectedSeats.size === 0) {
        showMessage("Please select at least one seat.");
        return;
    }

    bookButton.disabled = true;
    bookButton.textContent = "Booking...";

    const body = new URLSearchParams();

    body.append("scheduleId", scheduleId);
    body.append(
        "seatIds",
        Array.from(selectedSeats.keys()).join(",")
    );

    fetch(contextPath + "/bookSeat", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: body.toString()
    })
        .then(function (response) {

            if (response.redirected) {
                window.location.href = response.url;
                return null;
            }

            return response.text().then(function (html) {
                return {
                    ok: response.ok,
                    html: html
                };
            });
        })
        .then(function (result) {

            if (!result) {
                return;
            }

            document.open();
            document.write(result.html);
            document.close();
        })
        .catch(function (error) {

            console.error(error);
            showMessage("Booking failed. Please try again.");
            bookButton.disabled = false;
            bookButton.textContent = "Book Selected Seats";
        });
}

function showMessage(message) {
    seatMessage.innerHTML =
        '<div class="error-message">' +
        escapeHtml(message) +
        '</div>';
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
