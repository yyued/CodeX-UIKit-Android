package com.yy.codex.uikit

abstract class UITableViewDataSourceObject: UITableView.UITableViewDataSource {

    override fun titleForHeaderInSection(tableView: UITableView, section: Int): String? {
        return null
    }

    override fun titleForFooterInSection(tableView: UITableView, section: Int): String? {
        return null
    }

}

abstract class UITableViewDelegateObject: UITableView.UITableViewDelegate {

    override fun heightForRowAtIndexPath(tableView: UITableView, indexPath: NSIndexPath): Double {
        return tableView.rowHeight
    }

    override fun heightForHeaderInSection(tableView: UITableView, section: Int): Double {
        return 28.0
    }

    override fun heightForFooterInSection(tableView: UITableView, section: Int): Double {
        return 28.0
    }

    override fun didSelectRowAtIndexPath(tableView: UITableView, indexPath: NSIndexPath) {}

    override fun didDeselectRowAtIndexPath(tableView: UITableView, indexPath: NSIndexPath) {}

    override fun viewForHeaderInSection(tableView: UITableView, section: Int): UIView? {
        return null
    }

    override fun viewForFooterInSection(tableView: UITableView, section: Int): UIView? {
        return null
    }

    override fun willDisplayCell(tableView: UITableView, cell: UITableViewCell, indexPath: NSIndexPath) {}

    override fun didEndDisplayingCell(tableView: UITableView, cell: UITableViewCell, indexPath: NSIndexPath) {}

    override fun accessoryTypeForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell.AccessoryType? {
        return null
    }

    override fun shouldHighlightRow(tableView: UITableView, indexPath: NSIndexPath): Boolean? {
        return null
    }

    override fun didHighlightRow(tableView: UITableView, indexPath: NSIndexPath) {

    }

    override fun didUnhighlightRow(tableView: UITableView, indexPath: NSIndexPath) {

    }

    override fun scrollViewDidScroll(scrollView: UIScrollView) {}

    override fun scrollViewWillBeginDragging(scrollView: UIScrollView) {}

    override fun scrollViewDidEndDragging(scrollView: UIScrollView, willDecelerate: Boolean) {}

    override fun scrollViewWillBeginDecelerating(scrollView: UIScrollView) {}

    override fun scrollViewDidEndDecelerating(scrollView: UIScrollView) {}

}
