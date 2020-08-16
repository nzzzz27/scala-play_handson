(function(d, w) {
  d.addEventListener("DOMContentLoaded", () => {
    Array.from(
      d.getElementsByClassName("delete")
    ).forEach(action => {
      action.addEventListener("click", (e) => {
        e.currentTarget.parentNode.submit();
        e.stopPropagation();
      });
    });
  });

  Array.from(
    document.getElementsByClassName("card")
  ).forEach(card => {
    card.addEventListener("click", (e) => {
      location.href = e.currentTarget.getAttribute("data-href");
    })
  })
})(document, window)
