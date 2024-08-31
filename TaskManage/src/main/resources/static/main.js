(function() {
  /**
   * Easy selector helper function
   */
  const select = (el, all = false) => {
    el = el.trim()
    if (all) {
      return [...document.querySelectorAll(el)]
    } else {
      return document.querySelector(el)
    }
  }

  /**
   * Easy event listener function
   */
  const on = (type, el, listener, all = false) => {
    if (all) {
      select(el, all).forEach(e => e.addEventListener(type, listener))
    } else {
      select(el, all).addEventListener(type, listener)
    }
  }

  /**
   * Easy on scroll event listener 
   */
  const onscroll = (el, listener) => {
    el.addEventListener('scroll', listener)
  }
  
  /**
   * Sidebar toggle
  */
  if (select('.toggle-sidebar-btn')) {
    on('click', '.toggle-sidebar-btn', function(e) {
      select('body').classList.toggle('toggle-sidebar')
    })
  }

  /**
   * 編集モード trigger
  */
 
  /**
   * プロジェクトの編集
   */
  // id="edit-trigger-project" の編集ボタンが押されたときに動作
  const $modal = document.getElementById('modal-project');
  document.querySelector('#edit-trigger-project').addEventListener('click', function () {
    // ボタンの状態をトグルするため、編集モードかどうかを判定
    const isEditing = this.classList.contains('editing');
    
    // フォームの input と textarea を name 属性があるものだけ対象にする(idとcodeは除外)
    $modal.querySelectorAll('input[name]:not([id="edit-project-id"]):not([id="edit-code"]), textarea[name]').forEach(function (element) {
        if (isEditing) {
            element.setAttribute('disabled', 'disabled');
            element.classList.remove('form-control');
            element.classList.add('form-control-plaintext');
        } else {
            element.removeAttribute('disabled');
            element.classList.remove('form-control-plaintext')
            element.classList.add('form-control')
        }
    });

    // 更新ボタンの表示切替
    const $updateBtn = $modal.querySelector("#project-update-btn");
    if (isEditing) {
      $updateBtn.setAttribute('hidden', 'hidden'); // 非表示に戻す
    } else {
      $updateBtn.removeAttribute('hidden'); // hidden 属性を削除して表示
    }

    // コードの編集をできなくする
    if(!isEditing && $modal.querySelector("#collapseEditCode").classList.contains('show')){
      document.querySelector('#code-trigger').click();
    }
    
    // ボタンの状態をトグルする
    this.classList.toggle('editing');
  });
  
  // modal が閉じられるときに編集状態を解除
  $modal.addEventListener('hidden.bs.modal', function () {
    // どちらかの編集モードが有効の場合、プログラム的に解除
    const editTrigger = document.querySelector('#edit-trigger-project');
    if (editTrigger.classList.contains('editing')) {
      editTrigger.click();
    }else if($modal.querySelector("#collapseEditCode").classList.contains('show')){
      $modal.querySelector("#code-trigger").click();
    }
  });

  //update-btnの連動 
  document.querySelector("#project-update-btn").addEventListener('click', function(){
    document.querySelector("#project-update").submit();
  });




/**
 * プロジェクトコードの編集
 */
  //プロジェクトコード変更用collapseのイベント制御
  document.querySelector("#collapseEditCode").addEventListener('shown.bs.collapse', function(){
    const editTrigger = document.querySelector('#edit-trigger-project');
    if (editTrigger.classList.contains('editing')) {
      editTrigger.click();
    }
    $modal.querySelector("#edit-code").removeAttribute('disabled'); // disabled を削除
  });
  document.querySelector("#collapseEditCode").addEventListener('hide.bs.collapse', function(){
    $modal.querySelector("#edit-code").setAttribute('disabled', 'disabled'); // 再度非有効化
  });

  // 表示/非表示ボタンのクリックイベント
  document.querySelector("#password-toggle").addEventListener('click', function () {
    const $inputCode = $modal.querySelector("#edit-code");
    // パスワードのtypeをチェックし、表示/非表示を切り替え
    if ($inputCode.type === 'password') {
      $inputCode.type = 'text';  // パスワードを表示
    } else {
      $inputCode.type = 'password';  // パスワードを非表示
    }
  });
  
  // submit時にtype=passwordに戻す
  document.querySelector("#code-update-btn").addEventListener('click', function(){
    if($modal.querySelector("#edit-code").type === 'text'){
      document.querySelector("#password-toggle").click();
    }
  });



/**
 * タスクの編集
 */
  // id="edit-trigger" の編集ボタンが押されたときに動作 offcanvasScrolling
  const offcanvasElement = document.getElementById('offcanvasScrolling');
  document.querySelector('#edit-trigger').addEventListener('click', function () {
    // ボタンの状態をトグルするため、編集モードかどうかを判定
    const isEditing = this.classList.contains('editing');
    
    // フォームの input と textarea を name 属性があるものだけ対象にする
    offcanvasElement.querySelectorAll('input[name]:not([name="priority"]), textarea[name]').forEach(function (element) {
        if (isEditing) {
            // すでに編集モードなら非有効化に戻す
            element.setAttribute('disabled', 'disabled'); // 再度非有効化
            element.classList.remove('form-control'); // 通常クラスを削除
            element.classList.add('form-control-plaintext'); // 元のクラスに戻す
        } else {
            // 編集モードにする
            element.removeAttribute('disabled'); // disabled を削除
            element.classList.remove('form-control-plaintext'); // 元のクラスを削除
            element.classList.add('form-control'); // 通常クラスに変更
        }
    });

    // priority のラジオボタンに関する処理
    offcanvasElement.querySelectorAll('input[type="radio"][name="priority"]').forEach(function (radio) {
      if (!radio.checked) {
          // checked ではない場合のみ、有効/無効を切り替える
          if (isEditing) {
              radio.setAttribute('disabled', 'disabled'); // 再度非有効化
          } else {
              radio.removeAttribute('disabled'); // 無効化を解除
          }
      }
    });

    // select[name="assignedUserId"] の処理
    const selectElement = document.querySelector('select[name="assignedUserId"]');
    if (isEditing) {
        selectElement.setAttribute('disabled', 'disabled');
        selectElement.classList.remove('form-select');
        selectElement.classList.add('form-control-plaintext');
    } else {
        selectElement.removeAttribute('disabled');
        selectElement.classList.remove('form-control-plaintext');
        selectElement.classList.add('form-select');
    }

    // select 隣の input と button の hidden の切り替え
    const inputElement = selectElement.nextElementSibling; // input
    const buttonElement = inputElement.nextElementSibling; // button
    if (isEditing) {
        inputElement.setAttribute('hidden', 'hidden');
        buttonElement.setAttribute('hidden', 'hidden');
    } else {
        inputElement.removeAttribute('hidden');
        buttonElement.removeAttribute('hidden');
    }

    // 検索ボタンや他の hidden 要素を表示/非表示
    offcanvasElement.querySelectorAll('input[hidden][name], button[hidden][name]').forEach(function (element) {
        if (isEditing) {
            element.setAttribute('hidden', 'hidden'); // 非表示に戻す
        } else {
            element.removeAttribute('hidden'); // hidden 属性を削除して表示
        }
    });

    // 完了ボタンを非表示にする処理
    const completeButton = document.querySelector('#completeButton');
    if (isEditing) {
        completeButton.removeAttribute('hidden');
    } else {
        completeButton.setAttribute('hidden', 'hidden');
    }

    // d-none -> d-flex に切り替える処理
    const formActions = document.querySelector('#formActions');
    if (isEditing) {
        formActions.classList.add('d-none');
        formActions.classList.remove('d-flex');
    } else {
      formActions.classList.remove('d-none');
      formActions.classList.add('d-flex');
    }
    
    // ボタンの状態をトグルする
    this.classList.toggle('editing');
  });
  
  // offcanvas が閉じられるときに編集状態を解除
  offcanvasElement.addEventListener('hidden.bs.offcanvas', function () {
    // #edit-trigger のクリックイベントをプログラム的に発火
    const editTrigger = document.querySelector('#edit-trigger');
    
    if (editTrigger.classList.contains('editing')) {
        // #edit-trigger をクリックして編集状態を解除
        editTrigger.click();
    }
  });

  //完了ボタンと更新ボタンの処理
  offcanvasElement.querySelectorAll('.hidden-form-trigger').forEach(button => {
    button.addEventListener('click', function(event) {
      // ボタンの value を取得
      const buttonValue = event.target.value;

      // 隠れた submit ボタンの name 属性に値を設定
      offcanvasElement.getElementById('hidden-form-submit').name = buttonValue;

      // 隠れたフォームを送信
      offcanvasElement.getElementById('hidden-form-submit').click();
    });
  });


  // モーダルが表示される前に実行されるイベントリスナー
  document.querySelector("#project-info-btn").addEventListener("click", function () {
    // 押されたボタンからdata-*属性を取得
    const projectId = this.getAttribute("data-project-id");
    const name = this.getAttribute("data-name");
    const description = this.getAttribute("data-description");
    const firstTask = this.getAttribute("data-first-task");
    const progress = this.getAttribute("data-progress");
    const leader = this.getAttribute("data-leader");
    const createdAt = this.getAttribute("data-created-at");
    const updatedAt = this.getAttribute("data-updated-at");

    // それぞれのモーダルのフィールドに反映
    document.querySelector("#edit-project-id").value = projectId;
    document.querySelector("#edit-name").value = name;
    document.querySelector("#edit-description").value = description;
    document.querySelector("#edit-first-task").value = firstTask;
    document.querySelector("#edit-progress").style.width = `${progress}%`;
    document.querySelector("#edit-progress").ariaValueNow = progress;
    document.querySelector("#edit-progress-text").textContent = `${progress}%`;
    document.querySelector("#edit-leader").value = leader;
    document.querySelector("#edit-created-at").value = createdAt;
    document.querySelector("#edit-updated-at").value = updatedAt;
  });


  /**
   * offcanvasの表示時にformのvalueを挿入
   */

  // オフキャンバスが表示されるたびに、data-* 属性の値を反映する処理
  const taskEditForm = document.querySelector('#offcanvasScrolling form');
  document.querySelectorAll('.task-info-btn').forEach(button => {
    button.addEventListener('click', (event) => {
      // data-* 属性の値を取得
      const taskId = button.getAttribute('data-task-id');
      const title = button.getAttribute('data-title');
      const description = button.getAttribute('data-description');
      const assignedUserId = button.getAttribute('data-assigned-user-id');
      const assignedUserName = button.getAttribute('data-assigned-user-name');
      const deadline = button.getAttribute('data-deadline');
      const subCompleted = button.getAttribute('data-sub-completed');
      const subTotal = button.getAttribute('data-sub-total');
      const parentTitle = button.getAttribute('data-parent-title');
      const ownerName = button.getAttribute('data-owner-name');
      const createdAt = button.getAttribute('data-created-at');
      const updatedAt = button.getAttribute('data-updated-at');
      const completedAt = button.getAttribute('data-completed-at');
      const priority = button.getAttribute('data-priority');
      const state = button.getAttribute('data-state') === 'true'; 
      const subState = subCompleted === subTotal;


      
      // オフキャンバス内のフォーム要素を更新
      taskEditForm.querySelector('#form-task-id').value = taskId;
      taskEditForm.querySelector('#form-title').value = title;
      taskEditForm.querySelector('#form-description').value = description;
      taskEditForm.querySelector('#form-assigned-user-selected').value = assignedUserId;
      taskEditForm.querySelector('#form-assigned-user-selected').textContent = assignedUserName;
      taskEditForm.querySelector('#form-deadline').value = deadline;
      taskEditForm.querySelector('#form-sub-completed').value = subCompleted;
      taskEditForm.querySelector('#form-sub-total').value = subTotal;
      taskEditForm.querySelector('#form-parent-title').value = parentTitle;
      taskEditForm.querySelector('#form-owner-name').value = ownerName;
      taskEditForm.querySelector('#form-created-at').value = createdAt;
      taskEditForm.querySelector('#form-updated-at').value = updatedAt;
      taskEditForm.querySelector('#form-completed-at').value = completedAt;

      // 優先度に基づいてラジオボタンを設定
      taskEditForm.querySelectorAll('input[type="radio"]').forEach(radio => {
        radio.disabled = true; // すべてのラジオボタンを無効にする
      });
      
      const targetRadio = taskEditForm.querySelector(`input[type="radio"][value="${priority}"]`);
      if (targetRadio) {
          targetRadio.disabled = false; // 指定された優先度のラジオボタンを有効にする
          targetRadio.checked = true; // 指定された優先度のラジオボタンをチェック状態にする
      }

      //state, subStateによっての表示非表示
      const alert = document.querySelector('#completed-alert');
      const warningAlert = document.querySelector('#completed-alert-warning');
      alert.classList.add('d-none');
      warningAlert.classList.add('d-none');
      if(!subState && state){
        warningAlert.classList.remove('d-none');
      }else if(subState){
        alert.classList.remove('d-none');
      }

      taskEditForm.querySelector('#form-completed-at-div').classList.add('d-none');
      if(state){
        taskEditForm.querySelector('#form-completed-at-div').classList.remove('d-none');
      } 

      // 担当者の項目を初期化
      taskEditForm.querySelector('.search-input').value = '';
      taskEditForm.querySelector('.search-button').click();
      taskEditForm.querySelector('.search-select').selectedIndex = 0;
    });
  });

  /**
   * タスク作成offcanvasのvalue挿入
   */
  
  const taskNewForm = document.querySelector('#offcanvasNewTask form');
  document.querySelectorAll('.btn-add, .btn-new-task').forEach( button => {
      button.addEventListener('click', function() {
      // ボタンから data-* 属性の値を取得
      const parentId = button.getAttribute('data-parent-id');
      const parentTitle = button.getAttribute('data-parent-title');
      const ownerName = button.getAttribute('data-owner-name');
      
      // フォームの input 要素に値を設定
      taskNewForm.querySelector('#new-parent-id').value = parentId || '';
      taskNewForm.querySelector('#new-parent-title').value = parentTitle || '';
      taskNewForm.querySelector('#new-owner-name').value = ownerName || '';
  
      // フォームの他のフィールドをリセット
      taskNewForm.querySelector('#new-title').value = '';
      taskNewForm.querySelector('#new-description').value = '';
      taskNewForm.querySelector('#new-deadline').value = '';
      
      // ラジオボタンの選択をリセット（優先度フィールド）
      taskNewForm.querySelectorAll('input[name="priority"]').forEach(input => {
        if(input.value === '1'){
          input.checked = true;
        }else{
          input.checked = false;
        }
      });

      // 担当者の項目を初期化
      taskNewForm.querySelector('.search-input').value = '';
      taskNewForm.querySelector('.search-button').click();
      taskNewForm.querySelector('.search-select').selectedIndex = 0;

    });
  });
  


  /**
   * ユーザー検索ボタン処理
   */
  document.querySelectorAll('.search-button').forEach(function(button) {
    button.addEventListener('click', function() {
      // 対象のフォームを取得
      var form = button.closest('.search-form');
      var input = form.querySelector('.search-input');
      var select = form.querySelector('.search-select');
      
      // 入力フィールドの値を取得
      var inputValue = input.value.toLowerCase();
      
      // <select> 要素内のすべての <option> を取得
      var options = select.options;
      
      // すべての <option> をループ
      for (var i = 0; i < options.length; i++) {
        var optionText = options[i].textContent.toLowerCase();
        
        // 入力値が <option> のテキストに含まれているかをチェック
        if (optionText.includes(inputValue) || inputValue === '') {
          options[i].style.display = ''; // 表示
        } else {
          options[i].style.display = 'none'; // 非表示
        }
      }
    });
  });






  /**
   * priority-icon表示制御
   */
  const icons = document.querySelectorAll('.priority-icon').forEach(icon => {
    // data-status属性から状態を取得
    const status = parseInt(icon.getAttribute('data-priority'), 10);

    // クラスをクリアして、新しいクラスを追加
    icon.className = "bi fs-6";  // 基本クラスをリセット

    // 状態に応じたアイコンを設定
    switch (status) {
        case 2:
            icon.classList.add('bi-fast-forward-fill', 'text-info');
            break;
        case 3:
            icon.classList.add('bi-star-fill', 'text-warning');
            break;
        case 4:
            icon.classList.add('bi-exclamation-triangle-fill', 'text-danger');
            break;
        case 1:
        default:
            icon.style.display = 'none';  // アイコンなし
            break;
    }
  });


  /**
   * スクリーン遷移制御
  */
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(event) {

      if (this.hasAttribute('data-bs-toggle')) {
        return;
      }
      
      event.preventDefault();
  
      const targetId = this.getAttribute('href');
      const targetElement = document.querySelector(targetId);
      const headerOffset = 100; // 固定ヘッダーの高さに応じて調整
      const elementPosition = targetElement.getBoundingClientRect().top;
      const offsetPosition = elementPosition + window.scrollY - headerOffset;
  
      window.scrollTo({
        top: offsetPosition,
        behavior: 'smooth'  // スムーズなスクロール
      });
    });
  });

  //カスタムスクロールバーのhover制御
  document.querySelectorAll('.scrollable-content').forEach(element => {
      let hoverTimeout;

      element.addEventListener('mouseover', () => {
        // すべての .scrollable-content からクラスを削除
        document.querySelectorAll('.scrollable-content').forEach(el => {
          if (el !== element) { // 現在ホバーしている要素は除外
              el.classList.remove('scrollable-content-hover');
          }
        });


          // ホバークラスを追加
          element.classList.add('scrollable-content-hover');
      });

      element.addEventListener('mouseout', () => {
          // 既存のタイマーをクリア
          clearTimeout(hoverTimeout);

          // 指定時間後にホバークラスを削除
          hoverTimeout = setTimeout(() => {
              element.classList.remove('scrollable-content-hover');
          }, 300); // 300ミリ秒後にクラスを削除
      });
  });
  
  
})();