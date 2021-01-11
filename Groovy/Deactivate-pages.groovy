//Deactivte a page 
dryRun = true;
contentNode = getNode("/content")
internetSites = contentNode.getNodes("geometrix-*")
def pageNames = ["PageA","PageB","Page3"]
internetSites.each {
    try {
        it.recurse { node ->
            
            if (node.getProperty("jcr:primaryType").getString().equals("cq:Page")) {
                
                if(pageNames.contains(node.name)){
                    if (!dryRun){
                        deactivate(node.path)
                    }
                    println node.path
                }
                
                
            }
        }
    } catch(PathNotFoundException pnf) {
        println it.path + " not found"
    }
}