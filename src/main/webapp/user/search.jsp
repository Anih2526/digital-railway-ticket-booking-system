<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Trains</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user.css">
    <script>
        window.APP_CONTEXT_PATH = "${pageContext.request.contextPath}";
    </script>

    <script src="${pageContext.request.contextPath}/js/search.js?v=7" defer></script>
</head>
<body>

<header class="top-header">
    <div class="header-content header-row">
        <div>
            <h1>Digital Railway</h1>
            <p>Train Search</p>
        </div>

        <a class="header-link" href="${pageContext.request.contextPath}/user/dashboard.jsp">
            Dashboard
        </a>
    </div>
</header>

<main class="page-container search-page">

    <section class="content-card search-panel">

        <h2>Where do you want to go?</h2>
        <p class="intro-text">
            Search trains by route and journey date.
        </p>

        <div class="search-form">

            <div class="search-field">
                <label for="from">From</label>
                <input type="text"
                       id="from"
                       list="stationOptions"
                       placeholder="Enter departure station"
                       autocomplete="off">
            </div>

            <button type="button"
                    id="reverseButton"
                    class="swap-button"
                    title="Reverse stations"
                    aria-label="Reverse stations">
                ⇄
            </button>

            <div class="search-field">
                <label for="to">To</label>
                <input type="text"
                       id="to"
                       list="stationOptions"
                       placeholder="Enter destination station"
                       autocomplete="off">
            </div>

            <div class="search-field date-field">
                <label for="journeyDate">Journey Date</label>
                <input type="date"
                       id="journeyDate"
                       required>
            </div>

            <div class="search-field train-filter-field">
                <label for="trainQuery">Train (optional)</label>
                <input type="text"
                       id="trainQuery"
                       placeholder="Train number or name">
            </div>

            <datalist id="stationOptions"></datalist>

            <button type="button"
                    class="button primary-button search-button"
                    id="searchButton">
                Search Trains
            </button>
        </div>

        <div id="searchMessage"></div>

        <div id="dateStrip" class="date-strip" aria-label="Choose journey date"></div>

        <div class="results-toolbar">
            <div>
                <h3 id="routeSummary">Search results</h3>
                <p id="resultCount" class="toolbar-note"></p>
            </div>

            <div class="sort-box">
                <label for="sortBy">Sort by</label>
                <select id="sortBy">
                    <option value="departure">Departure</option>
                    <option value="arrival">Arrival</option>
                    <option value="duration">Duration</option>
                    <option value="fare">Fare</option>
                    <option value="availability">Available Seats</option>
                </select>
            </div>
        </div>

        <div id="trainResults"></div>

    </section>

</main>

</body>
</html>
