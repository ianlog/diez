import Foundation
import CoreGraphics

@objc(DEZDropShadow)
public final class DropShadow: NSObject, Decodable {
    @objc public internal(set) var offset: Point2D
    @objc public internal(set) var radius: CGFloat
    @objc public internal(set) var color: Color

    init(
        offset: Point2D,
        radius: CGFloat,
        color: Color
    ) {
        self.offset = offset
        self.radius = radius
        self.color = color
    }
}

extension DropShadow: ReflectedCustomStringConvertible {
    public override var description: String {
        return reflectedDescription
    }
}
