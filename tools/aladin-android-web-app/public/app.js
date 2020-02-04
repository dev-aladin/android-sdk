document.addEventListener("DOMContentLoaded", function(event) {
  document.getElementById('signin-button').addEventListener('click', function(event) {
    event.preventDefault()
    aladin.redirectToSignIn()
  })
  document.getElementById('signout-button').addEventListener('click', function(event) {
    event.preventDefault()
    aladin.signUserOut(window.location.href)
  })

  function showProfile(profile) {
    var person = new aladin.Person(profile)
    document.getElementById('heading-name').innerHTML = person.name() ? person.name() : "Nameless Person"
    if(person.avatarUrl()) {
      document.getElementById('avatar-image').setAttribute('src', person.avatarUrl())
    }
    document.getElementById('section-1').style.display = 'none'
    document.getElementById('section-2').style.display = 'block'
  }

  if (aladin.isUserSignedIn()) {
    var profile = aladin.loadUserData().profile
      showProfile(profile)
  } else if (aladin.isSignInPending()) {
    aladin.handlePendingSignIn().then(function(userData) {
      window.location = window.location.origin
    })
  }
})
