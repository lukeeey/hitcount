# hitcount.dev
[![Hits](https://hitcount.dev/p/lukeeey/hitcount.svg)](https://hitcount.dev/p/lukeeey/hitcount)

The simplest way to track the number of people viewing your GitHub projects!

### View Path Info
You can view info on a specific project ("path") by going to `hitcount.dev/p/GITHUB_NAME/REPO_NAME`. Example:
https://hitcount.dev/p/lukeeey/hitcount

### Get Path Info as JSON
Go to the path info URL from above and append `.json` to the end. Example:
https://hitcount.dev/p/lukeeey/hitcount.json

### Shields.io Badge
For even more customization of your badge, you can use the [shields.io](https://shields.io) API.

1. Take the path info URL from above and append `.shields.json`. Example: `https://hitcount.dev/p/lukeeey/hitcount.shields.json`
2. Go to the [shields.io endpoint badge page](https://shields.io/badges/endpoint-badge) and input the Shields JSON url above
3. Customize styles, etc.
4. You should end up with a URL somewhat similar to `https://img.shields.io/endpoint?url=https%3A%2F%2Fhitcount.dev%2Fp%2Flukeeey%2Fhitcount.shields.json&style=for-the-badge&labelColor=red`

To use this shields.io URL in your README, insert it like this (ensuring to replace the placeholders):

```
[![Hits](https://hitcount.dev/p/GITHUB_NAME/GITHUB_REPO.svg)](YOUR_SHIELDS_IO_URL)
```