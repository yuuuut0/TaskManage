(function () {
  /**
   * Easy selector helper function
   */
  const select = (el, all = false) => {
    el = el.trim();
    if (all) {
      return [...document.querySelectorAll(el)];
    } else {
      return document.querySelector(el);
    }
  };

  /**
   * Easy event listener function
   */
  const on = (type, el, listener, all = false) => {
    if (all) {
      select(el, all).forEach((e) => e.addEventListener(type, listener));
    } else {
      select(el, all).addEventListener(type, listener);
    }
  };

  /**
   * Easy on scroll event listener
   */
  const onscroll = (el, listener) => {
    el.addEventListener("scroll", listener);
  };

  /**
   * Sidebar toggle
   */
  if (select(".toggle-sidebar-btn")) {
    on("click", ".toggle-sidebar-btn", function (e) {
      select("body").classList.toggle("toggle-sidebar");
    });
  }

  /**
   * 編集モード trigger
   */

  var loginUserId = document.getElementById("loginUserId").textContent;

  /**
   * プロジェクトの編集
   */
  // id="edit-trigger-project" の編集ボタンが押されたときに動作
  const $modal = document.getElementById("modal-project");
  if (document.querySelector("#edit-trigger-project")) {
    document
      .querySelector("#edit-trigger-project")
      .addEventListener("click", function () {
        // ボタンの状態をトグルするため、編集モードかどうかを判定
        const isEditing = this.classList.contains("editing");

        // フォームの input と textarea を name 属性があるものだけ対象にする(idとcodeは除外)
        $modal
          .querySelectorAll(
            'input[name]:not([id="edit-project-id"]):not([class~="edit-code"]), textarea[name]'
          )
          .forEach(function (element) {
            if (isEditing) {
              element.setAttribute("disabled", "disabled");
              element.classList.remove("form-control");
              element.classList.add("form-control-plaintext");
            } else {
              element.removeAttribute("disabled");
              element.classList.remove("form-control-plaintext");
              element.classList.add("form-control");
            }
          });

        // 更新ボタンの表示切替
        const $updateBtn = $modal.querySelector("#project-update-btn");
        const $deleteBtn = $modal.querySelector("#project-delete-btn");
        if (isEditing) {
          $updateBtn.setAttribute("hidden", "hidden"); // 非表示に戻す
          $deleteBtn.setAttribute("hidden", "hidden"); // 非表示に戻す
        } else {
          $updateBtn.removeAttribute("hidden"); // hidden 属性を削除して表示
          $deleteBtn.removeAttribute("hidden"); // hidden 属性を削除して表示
        }

        // コードの編集をできなくする
        if (
          !isEditing &&
          $modal.querySelector("#collapseEditCode").classList.contains("show")
        ) {
          document.querySelector("#code-trigger").click();
        }

        // selectの初期化
        if (isEditing) {
          $modal.querySelector(".search-input").value = "";
          $modal.querySelector(".search-button").click();
          $modal.querySelector(".search-select").selectedIndex = 0;
        }

        //selectの表示非表示
        const $select = document.querySelector('select[class~="edit"]');
        if (isEditing) {
          $select.setAttribute("disabled", "disabled");
          $select.classList.remove("form-select");
          $select.classList.add("form-control-plaintext");
        } else {
          $select.removeAttribute("disabled");
          $select.classList.remove("form-control-plaintext");
          $select.classList.add("form-select");
        }
        // select 隣の input と button の hidden の切り替え
        const inputElement = $select.nextElementSibling; // input
        const buttonElement = inputElement.nextElementSibling; // button
        if (isEditing) {
          inputElement.setAttribute("hidden", "hidden");
          buttonElement.setAttribute("hidden", "hidden");
        } else {
          inputElement.removeAttribute("hidden");
          buttonElement.removeAttribute("hidden");
        }

        //注意書きの表示
        const formWarning = $modal.querySelector(".form-warning");
        if (isEditing) {
          formWarning.setAttribute("hidden", "hidden");
        } else {
          formWarning.removeAttribute("hidden");
        }

        // ボタンの状態をトグルする
        this.classList.toggle("editing");
      });
  }
  // modal が閉じられるときに編集状態を解除
  if ($modal) {
    $modal.addEventListener("hidden.bs.modal", function () {
      // どちらかの編集モードが有効の場合、プログラム的に解除
      const editTrigger = document.querySelector("#edit-trigger-project");
      if (editTrigger) {
        if (editTrigger.classList.contains("editing")) {
          editTrigger.click();
        } else if (
          $modal.querySelector("#collapseEditCode").classList.contains("show")
        ) {
          $modal.querySelector("#code-trigger").click();
        }
      }
    });
  }

  //update-btnの連動
  if (document.querySelector("#project-update-btn")) {
    document
      .querySelector("#project-update-btn")
      .addEventListener("click", function () {
        document.querySelector("#project-update").submit();
      });
  }

  /**
   * プロジェクトコードの編集
   */
  //プロジェクトコード変更用collapseのイベント制御
  if (document.querySelector("#collapseEditCode")) {
    document
      .querySelector("#collapseEditCode")
      .addEventListener("shown.bs.collapse", function () {
        const editTrigger = document.querySelector("#edit-trigger-project");
        if (editTrigger) {
          if (editTrigger.classList.contains("editing")) {
            editTrigger.click();
          }
        }
        $modal.querySelector(".edit-code").removeAttribute("disabled"); // disabled を削除
      });
    document
      .querySelector("#collapseEditCode")
      .addEventListener("hide.bs.collapse", function () {
        $modal.querySelector(".edit-code").setAttribute("disabled", "disabled"); // 再度非有効化
      });
  }

  // 表示/非表示ボタンのクリックイベント
  document.querySelectorAll(".password-toggle").forEach((toggleButton) => {
    toggleButton.addEventListener("click", function () {
      const $inputCode = this.parentElement.querySelector(".edit-code");
      // パスワードのtypeをチェックし、表示/非表示を切り替え
      if ($inputCode.type === "password") {
        $inputCode.type = "text"; // パスワードを表示
      } else {
        $inputCode.type = "password"; // パスワードを非表示
      }
    });
  });

  // submit時にtype=passwordに戻す
  //modal
  if (document.querySelector("#code-update-btn")) {
    document
      .querySelector("#code-update-btn")
      .addEventListener("click", function () {
        if ($modal.querySelector(".edit-code").type === "text") {
          $modal.querySelector(".password-toggle").click();
        }
      });
  }
  //createProject
  const $createProjectForm = document.querySelector("#create-project-form");
  if ($createProjectForm) {
    document
      .querySelector("#create-project-submit")
      .addEventListener("click", function () {
        if ($createProjectForm.querySelector(".edit-code").type === "text") {
          $createProjectForm.querySelector(".password-toggle").click();
        }
      });
    //joinProject
    const $joinProjectForm = document.querySelector("#join-project-form");
    document
      .querySelector("#join-project-submit")
      .addEventListener("click", function () {
        if ($joinProjectForm.querySelector(".edit-code").type === "text") {
          $joinProjectForm.querySelector(".password-toggle").click();
        }
      });
  }

  /**
   * タスクの編集
   */
  // id="edit-trigger" の編集ボタンが押されたときに動作 offcanvasScrolling
  const offcanvasElement = document.getElementById("offcanvasScrolling");
  const $editTrigger = document.querySelector("#edit-trigger");
  if ($editTrigger) {
    document
      .querySelector("#edit-trigger")
      .addEventListener("click", function () {
        // ボタンの状態をトグルするため、編集モードかどうかを判定
        const isEditing = this.classList.contains("editing");

        // フォームの input と textarea のクラスにeditがあるものだけ対象にする
        offcanvasElement
          .querySelectorAll(
            'input[class~="edit"]:not([type="radio"]):not([type="checkbox"]), textarea[class~="edit"]'
          )
          .forEach(function (element) {
            if (isEditing) {
              // すでに編集モードなら非有効化に戻す
              element.setAttribute("disabled", "disabled"); // 再度非有効化
              element.classList.remove("form-control"); // 通常クラスを削除
              element.classList.add("form-control-plaintext"); // 元のクラスに戻す
            } else {
              // 編集モードにする
              element.removeAttribute("disabled"); // disabled を削除
              element.classList.remove("form-control-plaintext"); // 元のクラスを削除
              element.classList.add("form-control"); // 通常クラスに変更
            }
          });

        //提出フラグに関するcheckboxの処理
        const checkbox = offcanvasElement.querySelector("#form-check");
        const ownerId = offcanvasElement.querySelector("#form-owner-id").value;
        if (loginUserId == ownerId) {
          if (isEditing) {
            checkbox.setAttribute("hidden", "hidden");
          } else {
            checkbox.removeAttribute("hidden");
          }
        }

        // priority のラジオボタンに関する処理
        offcanvasElement
          .querySelectorAll('input[type="radio"][class~="edit"]')
          .forEach(function (radio) {
            if (!radio.checked) {
              // checked ではない場合のみ、有効/無効を切り替える
              if (isEditing) {
                radio.setAttribute("disabled", "disabled"); // 再度非有効化
              } else {
                radio.removeAttribute("disabled"); // 無効化を解除
              }
            }
          });

        const connectFlg =
          offcanvasElement.querySelector("#form-connect-flg").value === "true";
        const submitFlg =
          offcanvasElement.querySelector("#form-submit-flg").checked;
        const completedFlg = offcanvasElement.querySelector(
          "#form-completed-flg"
        ).checked;
        if (!connectFlg && !(submitFlg && completedFlg)) {
          // select[class="edit"] の処理
          const selectElement = offcanvasElement.querySelector(
            'select[class~="edit"]'
          );
          if (isEditing) {
            selectElement.setAttribute("disabled", "disabled");
            selectElement.classList.remove("form-select");
            selectElement.classList.add("form-control-plaintext");
          } else {
            selectElement.removeAttribute("disabled");
            selectElement.classList.remove("form-control-plaintext");
            selectElement.classList.add("form-select");
          }

          // select 隣の input と button の hidden の切り替え
          const inputElement = selectElement.nextElementSibling; // input
          const buttonElement = inputElement.nextElementSibling; // button
          if (isEditing) {
            inputElement.setAttribute("hidden", "hidden");
            buttonElement.setAttribute("hidden", "hidden");
          } else {
            inputElement.removeAttribute("hidden");
            buttonElement.removeAttribute("hidden");
          }
        }

        // 検索ボタンや他の hidden 要素を表示/非表示
        offcanvasElement
          .querySelector("#task-update-form")
          .querySelectorAll(
            "input[hidden][name]:not([id='form-owner-id']), button[hidden][name]"
          )
          .forEach(function (element) {
            if (isEditing) {
              element.setAttribute("hidden", "hidden"); // 非表示に戻す
            } else {
              element.removeAttribute("hidden"); // hidden 属性を削除して表示
            }
          });

        // 完了ボタンを非表示にする処理
        const completeButton = document.querySelector("#completeButton");
        if (isEditing) {
          completeButton.removeAttribute("hidden");
        } else {
          completeButton.setAttribute("hidden", "hidden");
        }

        // d-none -> d-flex に切り替える処理
        const formActions = document.querySelector("#formActions");
        if (isEditing) {
          formActions.classList.add("d-none");
          formActions.classList.remove("d-flex");
        } else {
          formActions.classList.remove("d-none");
          formActions.classList.add("d-flex");
        }

        //submit用コメントをdisabledにする
        const comment = document.getElementById("form-comment");
        const commentBox = comment.parentElement;
        if (commentBox.className != "d-none") {
          if (isEditing) {
            comment.removeAttribute("disabled");
          } else {
            comment.setAttribute("disabled", "disabled");
          }
        }

        // ボタンの状態をトグルする
        this.classList.toggle("editing");
      });
  }

  // offcanvas が閉じられるときに編集状態を解除
  if (offcanvasElement) {
    offcanvasElement.addEventListener("hidden.bs.offcanvas", function () {
      // #edit-trigger のクリックイベントをプログラム的に発火

      if ($editTrigger.classList.contains("editing")) {
        // #edit-trigger をクリックして編集状態を解除
        $editTrigger.click();
      }
    });

    //完了ボタンと更新ボタンの処理
    document.querySelectorAll(".hidden-form-trigger").forEach((button) => {
      button.addEventListener("click", function (event) {
        // ボタンの value を取得
        const buttonValue = event.target.value;

        if (buttonValue == "submit") {
          const ownerId = document.getElementById("hidden-form-owner-id");
          const comment = document.getElementById("hidden-form-comment");

          ownerId.removeAttribute("disabled");
          comment.removeAttribute("disabled");
          comment.value = document.getElementById("form-comment").value;
        }

        // 隠れた submit ボタンの name 属性に値を設定
        document.getElementById("hidden-form-submit").name = buttonValue;

        // 隠れたフォームを送信
        document.getElementById("hidden-form-submit").click();
      });
    });
  }
  /**
   * 編集用offcanvas処理終了
   */

  /**
   * project編集モーダルが表示される前に実行されるイベントリスナー
   */
  if (document.querySelectorAll(".project-info-btn").length > 0) {
    document.querySelectorAll(".project-info-btn").forEach((button) => {
      button.addEventListener("click", function () {
        // 押されたボタンからdata-*属性を取得
        const name = this.getAttribute("data-name");
        const description = this.getAttribute("data-description");
        const firstTask = this.getAttribute("data-first-task");
        const taskDescription = this.getAttribute("data-task-description");
        const deadline = this.getAttribute("data-deadline");

        // それぞれのモーダルのフィールドに反映
        document.querySelector("#edit-name").value = name;
        document.querySelector("#edit-description").value = description;
        document.querySelector("#edit-first-task").value = firstTask;
        document.querySelector("#edit-task-description").value =
          taskDescription;
        document.querySelector("#edit-deadline").value = deadline;
      });
    });
  }

  /**
   * タスク編集offcanvasの表示時にformのvalueを挿入
   */

  // オフキャンバスが表示されるたびに、data-* 属性の値を反映する処理
  const taskEditForm = document.querySelector("#offcanvasScrolling");
  document.querySelectorAll(".task-info-btn").forEach((button) => {
    button.addEventListener("click", (event) => {
      // data-* 属性の値を取得
      const taskId = button.getAttribute("data-task-id");
      const title = button.getAttribute("data-title");
      const description = button.getAttribute("data-description");
      const assignedUserId = button.getAttribute("data-assigned-user-id");
      const assignedUserName = button.getAttribute("data-assigned-user-name");
      const submitFlg = button.getAttribute("data-submit-flg") === "true";
      const completedFlg = button.getAttribute("data-completed-flg") === "true";
      const connectFlg = button.getAttribute("data-connect-flg");
      const deadline = button.getAttribute("data-deadline");
      const subCompleted = button.getAttribute("data-sub-completed");
      const subTotal = button.getAttribute("data-sub-total");
      const parentTitle = button.getAttribute("data-parent-title");
      const ownerId = button.getAttribute("data-owner-id");
      const ownerName = button.getAttribute("data-owner-name");
      const createdAt = button.getAttribute("data-created-at");
      const updatedAt = button.getAttribute("data-updated-at");
      const completedAt = button.getAttribute("data-completed-at");
      const priority = button.getAttribute("data-priority");

      // オフキャンバス内のフォーム要素を更新
      taskEditForm.querySelector("#form-task-id").value = taskId;
      taskEditForm.querySelector("#disconnect-task-id").value = taskId;
      taskEditForm.querySelector("#hidden-form-task-id").value = taskId;
      taskEditForm.querySelector("#hidden-form-owner-id").value = ownerId;
      taskEditForm.querySelector("#form-title").value = title;
      taskEditForm.querySelector("#form-description").value = description;
      taskEditForm.querySelector("#form-assigned-user-selected").value =
        assignedUserId ? assignedUserId : "";
      taskEditForm.querySelector("#form-assigned-user-selected").textContent =
        assignedUserName;
      taskEditForm.querySelector("#form-completed-flg").checked = completedFlg;
      taskEditForm.querySelector("#form-submit-flg").checked = submitFlg;
      taskEditForm.querySelector("#form-connect-flg").value = connectFlg;
      taskEditForm.querySelector("#form-deadline").value = deadline;
      taskEditForm.querySelector("#form-sub-completed").value = subCompleted;
      taskEditForm.querySelector("#form-sub-total").value = subTotal;
      taskEditForm.querySelector("#form-parent-title").value = parentTitle;
      taskEditForm.querySelector("#form-owner-id").value = ownerId;
      taskEditForm.querySelector("#form-owner-name").value = ownerName;
      taskEditForm.querySelector("#form-created-at").value = createdAt;
      taskEditForm.querySelector("#form-updated-at").value = updatedAt;
      taskEditForm.querySelector("#form-completed-at").value = completedAt;

      if (document.querySelector("#connect-new-id")) {
        taskEditForm.querySelector("#connect-new-id").value = taskId;
        taskEditForm.querySelector("#connect-new-title").value = title;
        taskEditForm.querySelector("#connect-old-id").value = taskId;
        taskEditForm.querySelector("#connect-old-title").value = title;
      }

      // 優先度に基づいてラジオボタンを設定
      taskEditForm.querySelectorAll('input[type="radio"]').forEach((radio) => {
        radio.disabled = true; // すべてのラジオボタンを無効にする
      });

      const targetRadio = taskEditForm.querySelector(
        `input[type="radio"][value="${priority}"]`
      );
      if (targetRadio) {
        targetRadio.disabled = false; // 指定された優先度のラジオボタンを有効にする
        targetRadio.checked = true; // 指定された優先度のラジオボタンをチェック状態にする
      }

      //alertの表示切替
      const alertBox = taskEditForm.querySelector("#taskEdit-alert");
      const alertIcon = taskEditForm.querySelector("#taskEdit-alert-icon");
      const alertMessage = taskEditForm.querySelector(
        "#taskEdit-alert-message"
      );
      alertBox.className = "alert d-flex align-items-center";
      if (submitFlg) {
        if (completedFlg) {
          alertBox.classList.add("alert-warning");
          alertIcon.className = "bi bi-clock me-2";
          alertMessage.textContent = "承認を待っています。";
        } else if (subTotal != 0 && subCompleted == subTotal) {
          alertBox.classList.add("alert-warning");
          alertIcon.className = "bi bi-exclamation-triangle me-2";
          alertMessage.innerHTML =
            "サブタスクがすべて達成済みです。<br />承認申請を出しましょう！";
        } else {
          alertBox.classList.add("alert-info");
          alertIcon.className = "bi bi-exclamation-triangle me-2";
          alertMessage.textContent = "承認必須のタスクです。";
        }
      } else if (completedFlg) {
        if (subCompleted == subTotal) {
          alertBox.classList.add("alert-success");
          alertIcon.className = "bi bi-check-circle me-2";
          alertMessage.textContent = "達成済みのタスクです";
        } else {
          alertBox.classList.add("alert-warning");
          alertIcon.className = "bi bi-exclamation-triangle me-2";
          alertMessage.innerHTML =
            "達成済みですが、<br />未達成のサブタスクが残っています。";
        }
      } else {
        alertBox.className = "d-none";
      }

      taskEditForm
        .querySelector("#form-completed-at-div")
        .classList.add("d-none");
      if (!submitFlg && completedFlg) {
        taskEditForm
          .querySelector("#form-completed-at-div")
          .classList.remove("d-none");
      }

      const completeButton = document.getElementById("completeButton");
      const comment = document.getElementById("form-comment");
      const commentBox = comment.parentElement;
      comment.setAttribute("disabled", "disabled");
      commentBox.className = "d-none";
      if (loginUserId == assignedUserId && connectFlg == "false") {
        if (submitFlg) {
          if (completedFlg) {
            completeButton.className = "btn btn-warning hidden-form-trigger";
            completeButton.innerText = "申請を取り下げる";
            completeButton.value = "submit";
          } else {
            completeButton.className = "btn btn-primary hidden-form-trigger";
            completeButton.innerText = "申請";
            completeButton.value = "submit";
            comment.removeAttribute("disabled");
            commentBox.className = "col-12";
          }
        } else if (completedFlg) {
          if (subTotal == 0 || subTotal != subCompleted) {
            completeButton.className = "btn btn-warning hidden-form-trigger";
            completeButton.innerText = "未達成にする";
            completeButton.value = "complete";
          } else {
            completeButton.className = "d-none";
            completeButton.innerText = "";
          }
        } else {
          completeButton.className = "btn btn-success hidden-form-trigger";
          completeButton.innerText = "完了";
          completeButton.value = "complete";
        }
      } else {
        completeButton.className = "d-none";
        completeButton.innerText = "";
      }

      //担当者か責任者以外の場合編集トリガーをなくす
      if (loginUserId == ownerId || loginUserId == assignedUserId) {
        $editTrigger.className = "btn me-2";
      } else {
        $editTrigger.className = "d-none";
      }

      //プロジェクト接続済みの場合disconnect-buttonを表示
      if (document.querySelector("#disconnect-button")) {
        if (loginUserId == assignedUserId) {
          if (connectFlg == "true") {
            document.querySelector("#disconnect-button").className =
              "text-center my-3 d-flex flex-column";
            document.querySelector("#connect-form").className = "d-none";
          } else {
            document.querySelector("#disconnect-button").className = "d-none";
            document.querySelector("#connect-form").className =
              "text-center my-3";
          }
        } else {
          document.querySelector("#disconnect-button").className = "d-none";
          document.querySelector("#connect-form").className = "d-none";
        }
      }

      // 担当者の項目を初期化
      taskEditForm.querySelector(".search-input").value = "";
      taskEditForm.querySelector(".search-button").click();
      taskEditForm.querySelector(".search-select").selectedIndex = 0;
    });
  });

  /**
   * タスク作成offcanvasのvalue挿入
   */

  const taskNewForm = document.querySelector("#offcanvasNewTask form");
  document.querySelectorAll(".btn-add, .btn-new-task").forEach((button) => {
    button.addEventListener("click", function () {
      // ボタンから data-* 属性の値を取得
      const parentId = button.getAttribute("data-parent-id");
      const parentTitle = button.getAttribute("data-parent-title");
      const ownerName = button.getAttribute("data-owner-name");

      // フォームの input 要素に値を設定
      taskNewForm.querySelector("#new-parent-id").value = parentId || "";
      taskNewForm.querySelector("#new-parent-title").value = parentTitle || "";
      taskNewForm.querySelector("#new-owner-name").value = ownerName || "";

      // フォームの他のフィールドをリセット
      taskNewForm.querySelector("#new-title").value = "";
      taskNewForm.querySelector("#new-description").value = "";
      taskNewForm.querySelector("#new-deadline").value = "";
      taskNewForm.querySelector("#new-submit-flg").checked = false;

      // ラジオボタンの選択をリセット（優先度フィールド）
      taskNewForm.querySelectorAll('input[type="radio"]').forEach((input) => {
        if (input.value === "1") {
          input.checked = true;
        } else {
          input.checked = false;
        }
      });

      // 担当者の項目を初期化
      taskNewForm.querySelector(".search-input").value = "";
      taskNewForm.querySelector(".search-button").click();
      taskNewForm.querySelector(".search-select").selectedIndex = 0;
    });
  });

  /**
   * ユーザー検索ボタン処理
   */
  document.querySelectorAll(".search-button").forEach(function (button) {
    button.addEventListener("click", function () {
      // 対象のフォームを取得
      var form = button.closest(".search-form");
      var input = form.querySelector(".search-input");
      var select = form.querySelector(".search-select");

      // 入力フィールドの値を取得
      var inputValue = input.value.toLowerCase();

      // <select> 要素内のすべての <option> を取得
      var options = select.options;

      // すべての <option> をループ
      for (var i = 0; i < options.length; i++) {
        var optionText = options[i].textContent.toLowerCase();

        // 入力値が <option> のテキストに含まれているかをチェック
        if (optionText.includes(inputValue) || inputValue === "") {
          options[i].style.display = ""; // 表示
        } else {
          options[i].style.display = "none"; // 非表示
        }
      }
    });
  });

  /**
   * priority-icon表示制御
   */
  const icons = document.querySelectorAll(".priority-icon").forEach((icon) => {
    // data-status属性から状態を取得
    const status = parseInt(icon.getAttribute("data-priority"), 10);

    // クラスをクリアして、新しいクラスを追加
    icon.className = "bi fs-6"; // 基本クラスをリセット

    // 状態に応じたアイコンを設定
    switch (status) {
      case 2:
        icon.classList.add("bi-fast-forward-fill", "text-info");
        break;
      case 3:
        icon.classList.add("bi-star-fill", "text-warning");
        break;
      case 4:
        icon.classList.add("bi-exclamation-triangle-fill", "text-danger");
        break;
      case 1:
      default:
        icon.style.display = "none"; // アイコンなし
        break;
    }
  });

  /**
   * スクリーン遷移制御
   */
  document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
    anchor.addEventListener("click", function (event) {
      if (this.hasAttribute("data-bs-toggle")) {
        return;
      }

      event.preventDefault();

      const targetId = this.getAttribute("href");

      // 最初の文字 '#' を削除
      const cleanTargetId = targetId.slice(1);

      // '#' を削除したIDでターゲット要素を取得
      const targetElement = document.getElementById(cleanTargetId);

      if (targetElement) {
        const headerOffset = 100; // 固定ヘッダーの高さに応じて調整
        const sidebarOffset = 300;
        const elementRect = targetElement.getBoundingClientRect();
        const elementPositionY = elementRect.top;
        const elementPositionX = elementRect.left;

        // y方向とx方向のスクロール位置を計算
        const offsetPositionY =
          elementPositionY + window.scrollY - headerOffset;
        const offsetPositionX =
          elementPositionX + window.scrollX - sidebarOffset;

        // スクロール実行 (y方向とx方向を指定)
        window.scrollTo({
          top: offsetPositionY,
          left: offsetPositionX,
          behavior: "smooth", // スムーズなスクロール
        });
      } else {
        console.error(`Element with ID ${targetId} not found.`);
      }
    });
  });

  //カスタムスクロールバーのhover制御
  document.querySelectorAll(".scrollable-content").forEach((element) => {
    let hoverTimeout;

    element.addEventListener("mouseover", () => {
      // すべての .scrollable-content からクラスを削除
      document.querySelectorAll(".scrollable-content").forEach((el) => {
        if (el !== element) {
          // 現在ホバーしている要素は除外
          el.classList.remove("scrollable-content-hover");
        }
      });

      // ホバークラスを追加
      element.classList.add("scrollable-content-hover");
    });

    element.addEventListener("mouseout", () => {
      // 既存のタイマーをクリア
      clearTimeout(hoverTimeout);

      // 指定時間後にホバークラスを削除
      hoverTimeout = setTimeout(() => {
        element.classList.remove("scrollable-content-hover");
      }, 300); // 300ミリ秒後にクラスを削除
    });
  });

  if ($createProjectForm) {
    // 初期状態では「参加フォーム」が表示され、「新規フォーム」は非表示にする
    document.getElementById("join-project-form").style.display = "none";
    document.getElementById("create-project-form").style.display = "block";

    // 「参加」ボタンがクリックされた時
    document.getElementById("btnradio1").addEventListener("click", function () {
      document.getElementById("join-project-form").style.display = "none"; // 参加フォームを表示
      document.getElementById("create-project-form").style.display = "block"; // 新規フォームを非表示
    });

    // 「新規」ボタンがクリックされた時
    document.getElementById("btnradio2").addEventListener("click", function () {
      document.getElementById("join-project-form").style.display = "block"; // 参加フォームを非表示
      document.getElementById("create-project-form").style.display = "none"; // 新規フォームを表示
    });
  }

  if (document.getElementById("unapproved")) {
    // 初期状態では「参加フォーム」が表示され、「新規フォーム」は非表示にする
    document.getElementById("requests").style.display = "none";
    document.getElementById("unapproved").style.display = "block";

    // 「参加」ボタンがクリックされた時
    document.getElementById("btnradio1").addEventListener("click", function () {
      document.getElementById("requests").style.display = "none"; // 参加フォームを表示
      document.getElementById("unapproved").style.display = "block"; // 新規フォームを非表示
    });

    // 「新規」ボタンがクリックされた時
    document.getElementById("btnradio2").addEventListener("click", function () {
      document.getElementById("requests").style.display = "block"; // 参加フォームを非表示
      document.getElementById("unapproved").style.display = "none"; // 新規フォームを表示
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    const checkBox = document.getElementById("approvalToggle");
    if (checkBox) {
      checkBox.addEventListener("change", function () {
        toggleHistory(this);
      });
    }
  });
  function toggleHistory(checkbox) {
    if (checkbox.checked) {
      window.location.href = "/approval?log=true";
    } else {
      window.location.href = "/approval?log=false";
    }
  }

  const userToggleButtons = document.querySelectorAll(".user-toggle");
  if (userToggleButtons.length != 0) {
    userToggleButtons.forEach((button) => {
      button.addEventListener("click", function () {
        const inputId = button.getAttribute("data-input-id");
        const input = document.querySelector(
          `input[data-input-id='${inputId}']`
        );
        const btn = document.querySelector(`[data-btn-id='${inputId}']`);

        // 自分がすでに編集モードかどうかを確認
        const isEditable = !input.disabled;

        if (isEditable) {
          // すでに編集モードなら解除する
          input.classList.remove("form-control");
          input.classList.add("form-control-plaintext");
          input.disabled = true;
          btn.hidden = true;
        } else {
          // 他の編集状態を解除
          userToggleButtons.forEach((otherButton) => {
            if (otherButton !== button) {
              const otherInputId = otherButton.getAttribute("data-input-id");
              const otherInput = document.querySelector(
                `input[data-input-id='${otherInputId}']`
              );
              const otherBtn = document.querySelector(
                `[data-btn-id='${otherInputId}']`
              );

              otherInput.classList.remove("form-control");
              otherInput.classList.add("form-control-plaintext");
              otherInput.disabled = true;
              otherBtn.hidden = true;
            }
          });

          // 自分を編集可能にする
          input.classList.remove("form-control-plaintext");
          input.classList.add("form-control");
          input.disabled = false;
          btn.hidden = false;
        }
      });
    });
  }

  const containers = document.querySelectorAll(".border-start-container");
  if (containers.length > 0) {
    document.addEventListener("DOMContentLoaded", function () {
      // 初回計算
      recalculateBorders();

      // collapseイベントが発生するたびに再計算
      document.querySelectorAll(".collapse").forEach((collapseElement) => {
        // アイコンを変更するために対応するトリガーを探す
        const collapseId = collapseElement.id;
        const triggerElement = document.querySelector(
          `[aria-controls="${collapseId}"]`
        );
        const iconElement = triggerElement
          ? triggerElement.querySelector("i")
          : null;

        collapseElement.addEventListener("shown.bs.collapse", function () {
          recalculateBorders();
          if (iconElement) {
            iconElement.classList.remove("bi-plus-circle");
            iconElement.classList.add("bi-dash-circle");
          }
        });

        collapseElement.addEventListener("hidden.bs.collapse", function () {
          recalculateBorders();
          if (iconElement) {
            iconElement.classList.remove("bi-dash-circle");
            iconElement.classList.add("bi-plus-circle");
          }
        });
      });
    });
  }

  function recalculateBorders() {
    if (containers) {
      containers.forEach((container) => {
        const cardTop = bfsQuerySelector(container, ".card-top");
        const cardLast = bfsQuerySelector(container, ".card-last");
        const borderStart = bfsQuerySelector(container, ".border-start");

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
    }
  }

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
  }

  const disconnectTrigger = document.querySelector("#disconnect-trigger");
  const disconnectForm = document.querySelector("#disconnect-form");
  if (disconnectForm) {
    // モーダルが表示される前にトリガーされるイベント
    const modalElement = document.getElementById("disconnectModal");
    modalElement.addEventListener("show.bs.modal", function (event) {
      // ボタンのvalueを取得
      const button = event.relatedTarget; // モーダルを開いたボタン
      const buttonValue = button.value; // ボタンのvalue属性
      const modalBody = modalElement.querySelector(".modal-body");
      if(buttonValue == "disconnect"){
        // モーダルの内容を変更
        modalBody.textContent = "このタスクは現在のプロジェクトから切り離されます。";
        disconnectTrigger.innerText = "切り離す";
        disconnectTrigger.value = buttonValue;
      }else{
        modalBody.textContent = "統合されたプロジェクトは削除されます";
        disconnectTrigger.innerText = "統合する";
        disconnectTrigger.value = buttonValue;
      }
    });
    disconnectTrigger.addEventListener("click", function () {
      const value = this.value;
      document.getElementById("disconnect-form-params").name = value;
      disconnectForm.submit();
    });
  }
})();
