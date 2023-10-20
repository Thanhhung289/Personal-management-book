const totalCell = document.querySelectorAll('tr td:nth-child(6)');
// Kiểm tra giá trị total
totalCell.forEach(totalCell => {
    if (parseFloat(totalCell.innerText) < 0) {
        totalCell.parentNode.style.color = 'red';
    } else if (parseFloat(totalCell.innerText) == 0 || totalCell.innerText == 'Active' || totalCell.innerText == 'De Active') {
        totalCell.parentNode.style.color = 'black';
    } else {
        totalCell.parentNode.style.color = 'green';
    }
});

$(document).ready(function () {
    $(".sresizable-column").resizable({
        handles: 'e', // Chỉ cho phép kéo dãn chiều rộng từ phía bên phải
        minWidth: 50, // Chiều rộng tối thiểu của cột
        resize: function (event, ui) {
            // Khi cột được kéo dãn, điều chỉnh chiều rộng của các ô dữ liệu tương ứng
            var columnIndex = $(this).index();
            $("table tbody tr").each(function () {
                $(this).find("td:eq(" + columnIndex + ")").width(ui.size.width);
            });
        }
    });
});


document.getElementById("filterType").onchange = function () {
    var filterType = this.value;
    var rangeContainer = document.getElementById("rangeContainer");
    var filterValueStart = document.getElementById("filterValueStart");
    var filterValueEnd = document.getElementById("filterValueEnd");
    var errorSpan = document.getElementById("errorSpan");
    var searchButton = document.getElementById("searchButton");

    if (filterType === "total") {
        filterValueStart.type = "number";
        filterValueEnd.type = "number";
        filterValueStart.classList.remove("error");
        filterValueEnd.classList.remove("error");
        errorSpan.textContent = "";
        errorSpan.style.display = "none";
    } else if (filterType === "date") {
        filterValueStart.type = "date";
        filterValueEnd.type = "date";
        errorSpan.style.display = "none";
    }

    filterValueStart.oninput = function () {
        var startValue = filterValueStart.value;
        var endValue = filterValueEnd.value;

        if (filterType === "total" && parseFloat(startValue) > parseFloat(endValue)) {
            errorSpan.textContent = "Start value must be less than end value";
            errorSpan.style.display = "block";
            filterValueStart.classList.add("error");
            filterValueEnd.classList.add("error");
        } else if (filterType === "date" && startValue > endValue) {
            errorSpan.textContent = "Start date must be before end date";
            errorSpan.style.display = "block";
            filterValueStart.classList.add("error");
            filterValueEnd.classList.add("error");
        } else {
            errorSpan.textContent = "";
            errorSpan.style.display = "none";
            filterValueStart.classList.remove("error");
            filterValueEnd.classList.remove("error");
        }
        // toggleSearchButtonState();
    };

    filterValueEnd.oninput = function () {
        var startValue = filterValueStart.value;
        var endValue = filterValueEnd.value;

        if (filterType === "total" && parseFloat(startValue) > parseFloat(endValue)) {
            errorSpan.textContent = "Start value must be less than end value";
            errorSpan.style.display = "block";
            filterValueStart.classList.add("error");
            filterValueEnd.classList.add("error");
        } else if (filterType === "date" && startValue > endValue) {
            errorSpan.textContent = "Start date must be before end date";
            errorSpan.style.display = "block";
            filterValueStart.classList.add("error");
            filterValueEnd.classList.add("error");
        } else {
            errorSpan.textContent = "";
            errorSpan.style.display = "none";
            filterValueStart.classList.remove("error");
            filterValueEnd.classList.remove("error");
        }
        //toggleSearchButtonState();
    };

    // function toggleSearchButtonState() {
    //     var startValue = filterValueStart.value.trim();
    //     var endValue = filterValueEnd.value.trim();

    //     if (startValue === "" || endValue === "") {
    //      //   searchButton.disabled = true;
    //     } else {
    //         searchButton.disabled = false;
    //     }
    // }

    // toggleSearchButtonState();
};

document.getElementById("filterForm").onsubmit = function (event) {
    var startValue = document.getElementById("filterValueStart").value.trim();
    var endValue = document.getElementById("filterValueEnd").value.trim();

    if ((startValue != "" && endValue === "") || (startValue === "" && endValue != "")) {
        event.preventDefault();
        alert("Please enter both From and To values before submitting the form.");
    }
};


document.addEventListener('DOMContentLoaded', function () {
    var clearButton = document.getElementById('clearButton');
    clearButton.addEventListener('click', function () {
        // Xử lý sự kiện khi người dùng nhấp vào nút Clear Filter
        document.getElementById('filterForm').reset();
        document.getElementById('rangeContainer').classList.remove('active'); // Bỏ chọn active class cho rangeContainer (nếu có)
        document.getElementById("filterValueStart").value = "";
        document.getElementById("filterValueEnd").value = "";
        // window.location.href = '/Debtor/ListAll';
    });
});

