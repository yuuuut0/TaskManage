document.addEventListener("DOMContentLoaded", function () {
  const containers = document.querySelectorAll('.border-start-container');

  containers.forEach(container => {
    const cardTop = bfsQuerySelector(container, '.card-top');
    const cardLast = bfsQuerySelector(container, '.card-last');
    const borderStart = bfsQuerySelector(container, '.border-start');

    if (cardTop && cardLast && borderStart) {
      const rectTop = cardTop.getBoundingClientRect();
      const rectLast = cardLast.getBoundingClientRect();
      
      const startY = rectTop.top + window.scrollY;
      const endY = rectLast.top + window.scrollY;

      // 高さを計算し、`border-start`に適用
      const height = endY - startY;
      borderStart.style.height = `${height}px`;
      borderStart.style.top = `1.6rem`;
    }
  });
});

function bfsQuerySelector(root, selector) {
    const queue = [root]; // ルート要素をキューに追加

    while (queue.length > 0) {
        const element = queue.shift(); // キューから先頭の要素を取り出す

        // セレクターに一致する要素をチェック
        if (element.matches(selector)) {
            return element;
        }

        // 子要素をすべてキューに追加
        for (let i = 0; i < element.children.length; i++) {
            queue.push(element.children[i]);
        }
    }

    // 一致する要素が見つからなかった場合は null を返す
    return null;
};

