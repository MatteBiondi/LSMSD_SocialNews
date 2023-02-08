<nav class="navbar navbar-light bg-light justify-content-between">
    <div class="d-flex">
        <div class="btn-group" role="group">
            <button class="nav-button btn btn-outline-primary" type="button">
                <i class="fas fa-home"></i>
            </button>
            <button class="nav-button btn btn-outline-primary" type="button">
                <i class="fas fa-chart-bar"></i>
            </button>
            <button class="nav-button btn btn-outline-primary" type="button">
                <i class="fas fa-cog"></i>
            </button>
        </div>
        <form class="form-inline">
            <select class="form-control mr-sm-2" id="category-select" required>
                <option id="title-option" disabled selected hidden></option>
                <option value="reader">Reader</option>
                <option value="reporter">Reporter</option>
            </select>
            <input class="form-control mr-sm-2" type="text" placeholder="Search">
            <button class="nav-button btn btn-outline-primary my-2 my-sm-0 search-button" type="submit">
                <i class="fas fa-search"></i>
            </button>
        </form>
        <a class="navbar-brand" href="#" >Social News</a>
    </div>
</nav>

<script>
    document.querySelector('.search-button').addEventListener('click', function() {
        const select = document.querySelector('select');
        if (!select.value) {
            event.preventDefault();
            let titleOption = document.querySelector('#title-option');
            titleOption.textContent = "Select category";
        }
    });
</script>
