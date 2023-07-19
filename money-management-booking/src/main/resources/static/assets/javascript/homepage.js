let items = document.querySelectorAll('.item');
let nav = document.getElementById('nav');
document.addEventListener('scroll', (event) => {
    if(window.scrollY > 500){
        nav.classList.add('tofixed');
    }else{
        nav.classList.remove('tofixed');
    }
    items.forEach(item =>{
        if(item.offsetTop - window.scrollY < 350){
            item.classList.add('active');
        }
    })
})

document.getElementById('next').onclick = function(){
    const widthItem = document.querySelector('.item').offsetWidth;
    document.getElementById('formList').scrollLeft += widthItem;
}
document.getElementById('prev').onclick = function(){
    const widthItem = document.querySelector('.item').offsetWidth;
    document.getElementById('formList').scrollLeft -= widthItem;
}