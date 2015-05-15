var buzz = [
  'Long live that *',
  'Give that * a profile',
  'Become a library of *',
  'Give * persistent profiles',
  'Overview of all your *',
  'All * have a value',
  'Don\'t throw that * away',
  'Sell that * to a friend',
  'Let friends borrow your *',
  'Show that * to your friends',
  'Browse friends * all day'
];

var currBuzz = buzz[Math.floor(Math.random()*buzz.length)];

document.getElementById('buzz').innerHTML = currBuzz;  
  
