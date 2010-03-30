;;;{{{ Callbacks for Hook
(defun jde-transmogrify-hook-show-reference (str)
  (set-buffer "*Transmogrify Show References*")
  (insert str)
)


(defun jde-transmogrify-hook-open-file (sourceFileName)
  (message "jde-transmogrify-hook-open-file %s" sourceFileName)
  (find-file sourceFileName)
  (pop-to-buffer (current-buffer))
)

(defun jde-transmogrify-hook-get-current-file ()
  "Gets the file name of the currently focused file."
  (message "jde-transmogrify-hook-get-current-file")
  (buffer-file-name)
)

(defun jde-transmogrify-hook-get-user-input (prompt title)
  "Prompts for and returns user input
   * @return String entered from user
   * @param prompt question to ask the user
   * @param title summary of the prompt"
  (message "jde-transmogrify-hook-get-user-input")
  (read-string (concat title " - " prompt ":"))
)

(defun jde-transmogrify-hook-display-message (title message)
  (message "jde-transmogrify-hook-display-message")
  (message (concat title " : " message))
)

(defun jde-transmogrify-hook-display-exception (stackTrace description)
  (message "jde-transmogrify-hook-display-exception")
  (jde-transmogrify-display 
   "*Transmogrify Exception*" 
   (concat description ":\n" stackTrace) 
   nil)
)

(defun jde-transmogrify-hook-get-text ()
  "Returns the entire focused source file"
  (message "jde-transmogrify-hook-get-text")
  (buffer-substring-no-properties (point-min) (point-max))
)

(defun jde-transmogrify-hook-get-line (lineNumber)
  "Retrieves a line from the focused file."
  (message "jde-transmogrify-hook-get-line %s" lineNumber)
  (save-excursion
    (goto-line (string-to-number lineNumber))
    (beginning-of-line)
    (push-mark (point) t t)
    (forward-line 1)
    (buffer-substring-no-properties (region-beginning) (region-end))
    )
)

(defun jde-transmogrify-hook-select-text-lines (startLineNumber startOffset endLineNumber endOffset)
  "Selects a bunch of text.
   @parm startLineNumber the line number you wish to start selection on.
   @parm startOffset the position on the line where you with selection to start.
   @parm endLineNumber the line number you wish to end selection on.
   @parm endOffset the position on the line where you with selection to end."
  (message "jde-transmogrify-hook-select-text-lines %s %s %s %s" startLineNumber startOffset endLineNumber endOffset)
  (save-excursion
    (goto-line (string-to-number startLineNumber))
    (beginning-of-line)
    (forward-char (string-to-number startOffset))
    (push-mark (point) t t)
    (goto-line (string-to-number endLineNumber))
    (forward-char (string-to-number endOffset))
    (buffer-substring-no-properties (region-beginning) (region-end))
    )
)

(defun jde-transmogrify-hook-select-text-region (startPos endPos)
  "Selects a bunch of text.
   @parm startPos the charcter you wish to start selection on
   @parm endPos the character you wish to end selection on."
  (message "jde-transmogrify-hook-select-text-region %s %s" startPos endPos)
  (goto-char (string-to-number startPos))
  (push-mark (point) t t)
  (goto-char (string-to-number endPos))
)

(defun jde-transmogrify-hook-de-select-text ()
  "Deselects text"
  (message "jde-transmogrify-hook-de-select-text")
  (deactivate-mark)
)

(defun jde-transmogrify-hook-get-selected-text ()
  "Returns the selected text in the form of a String"
  (message "jde-transmogrify-hook-get-selected-text")
  (buffer-substring-no-properties (region-beginning) (region-end))
)

(defun jde-transmogrify-hook-get-selected-lines ()
  "Returns the selected lines. The lines are concatenated in one String, where
   each line is terminated by a \n\r"
  (message "jde-transmogrify-hook-get-selected-lines")
  (buffer-substring-no-properties (region-beginning) (region-end))
)

(defun jde-transmogrify-hook-get-caret-line-number ()
  " Returns the 1 based line number of where the cursor is.
    @return int the line number that the cursor is on."
  (message "jde-transmogrify-hook-get-caret-line-number")
  (count-lines 1 (point))
)

(defun jde-transmogrify-hook-get-caret-offset ()
 "Returns the 1 based offset of where the cursor is.
  @return int the offset of where on the line the cursor is."
  (message "jde-transmogrify-hook-get-caret-offset")
 (1+ (current-column))
)

(defun jde-transmogrify-hook-get-caret-pos ()
  "Returns the 0 based position of where the cursor is.
   @return int the position of where the cursor is in terms of total characters "
  (message "jde-transmogrify-hook-get-caret-pos")
  (point)
)

(defun jde-transmogrify-hook-get-selection-start () 
  (message "jde-transmogrify-hook-get-selection-start")
  (region-beginning)
)

(defun jde-transmogrify-hook-get-selection-end () 
  (message "jde-transmogrify-hook-get-selection-end")
  (region-end)
)

(defun jde-transmogrify-hook-set-caret-line-column (lineNumber offset)
  "Sets the 0 based position of where the cursor is.
   @parm lineNumber the line number of where the cursor is to be set.
   @parm offset the position on the line where the cursor is to be set."
  (message "jde-transmogrify-hook-set-caret-line-column %s %s" lineNumber offset)
  (goto-line lineNumber)
  (beginning-of-line)
  (forward-char (1- offset))
)

(defun jde-transmogrify-hook-set-caret-pos (pos) 
  "Sets the 0 based position of where the cursor is.
   @parm pos the position where the cursor is to be set in terms of total
   characters."
  (message "jde-transmogrify-hook-set-caret-pos %s" pos)
  (goto-char pos)
)
;;;}}}
