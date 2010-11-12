<div>
<?php
  echo $form->create('Task', array('type' => 'POST', 'action' => 'add'));
  echo $form->label(null, 'タイトル');
  echo $form->text('Task.title');
  echo $form->submit('追加');
  echo $form->end();
?>
</div>
<div>
  <table>
    <tr>
      <th>ID</th>
      <th>タイトル</th>
      <th>登録日</th>
      <th>更新日</th>
    </tr>
    <?php
      foreach ($tasks as $task) {
        $t = $task['tasks'];
    ?>
    <tr>
      <td><?php echo $t['task_id']; ?></td>
      <td><?php echo $t['title']; ?></td>
      <td><?php echo $t['created']; ?></td>
      <td><?php echo $t['modified']; ?></td>
    </tr>
    <?php
      }
    ?>
  </table>
</div>
