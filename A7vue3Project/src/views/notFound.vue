<template>
    <div class="page-wrapper">
        <div class="container">
            <h1>404</h1>
            <h2>没有找到该网页</h2>
            <p>您所寻找的网页似乎不存在哦</p>
            <a href="#" class="button" id="go-back">返回</a>
        </div>
    </div>
</template>

<script lang="ts" setup>
document.addEventListener('DOMContentLoaded', () => {
    const goBackButton = document.getElementById('go-back') as HTMLAnchorElement;

    goBackButton.addEventListener('click', (e: MouseEvent) => {
        e.preventDefault();
        window.history.back();
    });
});
</script>

<style scoped>
.page-wrapper {
    font-family: 'Poppins', sans-serif;
    margin: 0;
    padding: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: #f8f9fa;
    background-image: url('https://source.unsplash.com/random?404');
    background-size: cover;
    background-position: center;
}

.container {
    text-align: center;
    background-color: rgba(255, 255, 255, 0.8);
    padding: 40px;
    border-radius: 10px;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

h1 {
    font-size: 120px;
    margin: 0;
    color: #343a40;
    background: -webkit-linear-gradient(#f8f9fa, #6c757d);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

h2 {
    font-size: 40px;
    margin-bottom: 20px;
    color: #6c757d;
}

p {
    font-size: 20px;
    margin-bottom: 30px;
    color: #495057;
}

.button {
    display: inline-block;
    padding: 15px 30px;
    background-color: #007bff;
    color: white;
    text-decoration: none;
    border-radius: 5px;
    transition: background-color 0.3s ease;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    font-weight: 600;
}

.button:hover {
    background-color: #0056b3;
}
</style>