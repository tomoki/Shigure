(disable-theme 'zenburn)
(global-hl-line-mode -1)

(defface shigure-evaluated-accepted-face
  '((t :underline (:color "green3")))
  "the animations which was executed")

(defface shigure-evaluated-normal-face
  '((t :underline (:color "blue3")))
  "the animations which was executed")

(defface shigure-evaluated-declined-face
  '((t :underline (:color "red3")))
  "the animations which was executed")

(load-file "~/Dropbox/codes/shigure/testing/src/main/resources/shigure.el")
