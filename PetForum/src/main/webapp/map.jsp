<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="WEB-INF/header.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pets Marker Map</title>
    <style>
        body {
            font-family: 'Georgia', serif; 
            background-color: #2C422D; 
            color: #23363D; 
            margin: 0;
            padding: 20px;
            font-size: 18px;
        }
        .map-container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 30px;
            background-color: #ffffff;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            border: 1px solid #e0e0e0;
        }
        .button-container {
            margin-bottom: 20px;
        }
        .button {
            margin-right: 10px;
            padding: 10px 20px;
            background-color: #5cb85c;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }
        .button:hover {
            background-color: #4cae4c;
        }
        #eventList {
            margin-top: 20px;
        }

    </style>
</head>
<body>
    <div class="map-container">
        <h3>Animal Map - Where to find the animals?</h3>
        <div class="button-container">
            <button class="button" id="reportAnimalBtn">Report Animal</button>
            <button class="button" id="deleteSpotBtn">Delete Spot</button>
        </div>
        <div id="map" style="height: 500px; width: 100%;"></div>
        <div id="eventList">
            <h4>Events:</h4>
            <ul id="eventListUl"></ul> <!-- List for displaying events -->
        </div>
    </div>

    <script>
        let map;
        let markers = {};
        let infoWindow;
        let reportMode = false;  // Track report mode
        let deleteMode = false;  // Track delete mode
        let clickListener; // Store the click listener
        
        function initMap() {
	            const ntu = { lat: 1.3472, lng: 103.6825 };

            map = new google.maps.Map(document.getElementById("map"), {
                zoom: 16,
                center: ntu,
            });

            infoWindow = new google.maps.InfoWindow();

            // Load existing markers
            fetch("<%= request.getContextPath() %>/markers")
                .then(response => response.json())
                .then(data => {
                    data.forEach(marker => {
                        const position = { lat: marker.lat, lng: marker.lng };
                        const markerObj = addMarker(position, marker.animalName);
                        bindMarkerEvents(markerObj, marker.animalName, marker.id);
                    });
                    loadAllEvents(); // Load all events initially
                });

            // Add a listener for the button click
            document.getElementById('reportAnimalBtn').addEventListener('click', toggleReportMode);
            document.getElementById('deleteSpotBtn').addEventListener('click', toggleDeleteMode);
        }

        function toggleReportMode() {
            reportMode = !reportMode;  // Toggle report mode
            deleteMode = false;  // Ensure delete mode is off
            updateButtonStyles();

            // If entering report mode, add click listener; if exiting, remove it
            if (reportMode) {
                addMapClickListener();
            } else {
                removeMapClickListener();
            }
        }

        function toggleDeleteMode() {
            deleteMode = !deleteMode;  // Toggle delete mode
            reportMode = false;  // Ensure report mode is off
            updateButtonStyles();

            // If entering delete mode, add click listener; if exiting, remove it
            if (deleteMode) {
                addMapClickListener();
            } else {
                removeMapClickListener();
            }
        }

        function addMapClickListener() {
            // Add a click listener for adding markers
            clickListener = map.addListener("click", function(event) {
                if (reportMode) {
                    const lat = event.latLng.lat();
                    const lng = event.latLng.lng();
                    const animalName = prompt("Enter the animal's name:");

                    if (!animalName) {
                        alert("Animal name is required!");
                        return;
                    }

                    const position = { lat: lat, lng: lng };

                    fetch("<%= request.getContextPath() %>/saveMarker", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ lat: lat, lng: lng, animalName: animalName })
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.status === "success") {
                        	window.location.href = "map.jsp";
                        } else {
                            alert("Error saving marker.");
                        }
                    });
                }
            });
        }

        function removeMapClickListener() {
            if (clickListener) {
                google.maps.event.removeListener(clickListener); // Remove the click listener
                clickListener = null; // Clear the listener
            }
        }

        function addMarker(location, animalName) {
            const marker = new google.maps.Marker({
                position: location,
                map: map,
                title: animalName || 'Unnamed Animal'
            });

            const markerId = location.lat + "_" + location.lng;
            markers[markerId] = marker;
            return marker;
        }

        function bindMarkerEvents(marker, animalName, markerId) {
            google.maps.event.addListener(marker, "click", function () {
                if (!reportMode && !deleteMode) {
                    const contentString =
                        '<div>' +
                        '<h1>' + animalName + '</h1>' +
                        '<div id="bodyContent">' +
                        "<p><b>Click to filter events for this marker.</b></p>" +
                        '<button id="addEventBtn" style="background-color:#5cb85c;color:white;border:none;padding:5px 10px;border-radius:5px;cursor:pointer;">Add Event</button>' +
                        "</div>" +
                        "</div>";
                    infoWindow.setContent(contentString);
                    infoWindow.open(map, marker);
                    filterEventsByMarker(markerId);
                    
                    // Add event listener to load all events when the info window is closed
                    google.maps.event.addListener(infoWindow, "closeclick", function() {
                        loadAllEvents(); // Reload all events when the info window is closed
                    });
                    
                    // Add click listener to the "Add Event" button
                    google.maps.event.addListenerOnce(infoWindow, 'domready', function() {
                        document.getElementById('addEventBtn').addEventListener('click', function() {
                            addEvent(markerId); // Call the addEvent function
                        });
                    });
                } else if (!reportMode && deleteMode) {
                    google.maps.event.addListener(marker, "click", function (event) {
                        const lat = event.latLng.lat();
                        const lng = event.latLng.lng();
                        const markerId = `${lat}_${lng}`;

                        // Remove from the map and the database
                        marker.setMap(null);
                        delete markers[markerId];

                        fetch("<%= request.getContextPath() %>/deleteMarker", {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({ lat: lat, lng: lng })
                        })
                        .then(response => response.json())
                        .then(data => {
                            if (data.status !== "success") {
                                alert("Error deleting marker");
                            } else {
                            	window.location.href = "map.jsp";
                            }
                        });
                    });
                }
                
            });
        }
        function addEvent(markerId) {
            const title = prompt("Enter event title:");
            const content = prompt("Enter event content:");

            if (title && content) {
                // Send data to the server
                fetch("<%= request.getContextPath() %>/events", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                    },
                    body: new URLSearchParams({
                        title: title,
                        content: content,
                        markerId: markerId
                    })
                })
                .then(response => {
                    if (response.ok) {
                        alert("Event added successfully!");
                        window.location.href = "map.jsp";
                    } else {
                        alert("Failed to add event. Please try again.");
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("An error occurred while adding the event.");
                });
            } else {
                alert("Both title and content are required!");
            }
        }
     // Function to confirm deletion
        function confirmDelete(eventId) {
            if (confirm("Are you sure you want to delete this event?")) {
                fetch("<%= request.getContextPath() %>/events?deleteId=" + eventId, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).then(response => {
                    if (response.ok) {
                        alert("Event deleted successfully.");
                        loadAllEvents(); // Reload events after deletion
                    } else if (response.status === 401) {
                        throw new Error("Invalid user type.");
                    } else {
                        throw new Error("Delete failed.");
                    }
                }).catch(error => {
                    alert(error.message); // Show error message to user
                });
            }
        }
     
        function loadAllEvents() {
            fetch("<%= request.getContextPath() %>/events")
                .then(response => response.json())
                .then(data => {
                    const eventListUl = document.getElementById("eventListUl");
                    eventListUl.innerHTML = "";
                    data.forEach(event => {
                        const li = document.createElement("li");
                        li.textContent = event.title + " - " + event.content;

                        // Create a delete button
	                    const deleteButton = document.createElement("button");
	                    deleteButton.textContent = "Delete";
	                    deleteButton.style.backgroundColor = "#d9534f"; // Red color for delete button
	                    deleteButton.style.color = "white";
	                    deleteButton.style.border = "none";
	                    deleteButton.style.borderRadius = "5px";
	                    deleteButton.style.marginLeft = "10px";
	                    deleteButton.style.cursor = "pointer";
	
	                    // Add click event to delete button
	                    deleteButton.addEventListener("click", function() {
	                        confirmDelete(event.eventId); // Call confirmDelete with event id
	                    });
	
	                    // Append the delete button to the list item
	                    li.appendChild(deleteButton);
                        eventListUl.appendChild(li);
                    });
                });
        }

        function filterEventsByMarker(markerId) {
            fetch("<%= request.getContextPath() %>/events?markerId=" + markerId)
                .then(response => response.json())
                .then(data => {
                    const eventListUl = document.getElementById("eventListUl");
                    eventListUl.innerHTML = "";
                    data.forEach(event => {
                        const li = document.createElement("li");
                        li.textContent = event.title + " - " + event.content;

                        // Create a delete button
	                    const deleteButton = document.createElement("button");
	                    deleteButton.textContent = "Delete";
	                    deleteButton.style.backgroundColor = "#d9534f"; // Red color for delete button
	                    deleteButton.style.color = "white";
	                    deleteButton.style.border = "none";
	                    deleteButton.style.borderRadius = "5px";
	                    deleteButton.style.marginLeft = "10px";
	                    deleteButton.style.cursor = "pointer";
	
	                    // Add click event to delete button
	                    deleteButton.addEventListener("click", function() {
	                        confirmDelete(event.eventId); // Call confirmDelete with event id
	                    });
	
	                    // Append the delete button to the list item
	                    li.appendChild(deleteButton);
                        eventListUl.appendChild(li);
                    });
                });
        }

        function updateButtonStyles() {
            document.getElementById('reportAnimalBtn').style.backgroundColor = reportMode ? '#d9534f' : '#5cb85c';
            document.getElementById('reportAnimalBtn').innerText = reportMode ? 'Cancel Reporting' : 'Report Animal';
            document.getElementById('deleteSpotBtn').style.backgroundColor = deleteMode ? '#d9534f' : '#5cb85c';
            document.getElementById('deleteSpotBtn').innerText = deleteMode ? 'Cancel Deleting' : 'Delete Spot';
        }

        window.initMap = initMap;
    </script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBo7woVbpPSzfCbVrHr_lDjTCtnBNAuYhA&callback=initMap" async defer></script>
</body>
</html>
