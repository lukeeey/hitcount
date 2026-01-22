<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="author" content="luke@glitch.je">
    <meta name="description" content="The simplest way to track the number of people viewing your GitHub projects!">

    <title>HitCount.dev</title>
    
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap">

    <style>
        <#include "static/css/project.css">
    </style>

    <!-- Google tag (gtag.js) -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=G-KJVLW2RSW1"></script>
    <script>
      window.dataLayer = window.dataLayer || [];
      function gtag(){dataLayer.push(arguments);}
      gtag('js', new Date());

      gtag('config', 'G-KJVLW2RSW1');
    </script>
</head>

<body>
     <div class="navbar">
        <a class="navbar-title" href="/">Create your own Hit Counter!</a>

        <div class="donate-wrapper">
            <iframe class="gh-sponsors" src="https://github.com/sponsors/lukeeey/button" title="Sponsor lukeeey" height="32" width="114" style="border: 0; border-radius: 6px;"></iframe>
            <a href="https://www.buymeacoffee.com/lukeeey" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 40px !important;" ></a>
        </div>
    </div>
    
    <div class="main">
        <h1 class="leaderboard-title">Leaderboard</h1>

        <div class="stats-wrapper">
            <div class="stat">
                <p class="stat-title">Total projects</p>
                <p class="stat-value">${totalProjects}</p>
            </div>
        </div>

        <#if items?has_content>
            <div class="leaderboard">
                <#list items as item>
                    <a href="/p/${item.path}" class="leaderboard-item">
                        <div class="leaderboard-item-hits">${item.hits}</div>
                        <div class="leaderboard-item-path">${item.path}</div>
                    </a>
                </#list>
            </div>
        <#else>
            <p>No projects to show</p>
        </#if>
    </div>
</body>

</html>